using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace control_test
{
    class graphicView : Control
    {
        struct dragBorder
        {
            public graphicRegionView _upRegion;
            public graphicRegionView _downRegion;
            public RectangleF _border;
        }

        enum DRAG_MOUSE_STATUS
        {
            DOWN,
            MOVE,
            UP,
        }
        
        Pen _dragPen = new Pen(Color.Red);

        Graphics _gfx;
        BufferedGraphics _gfxBuffer;
        // 画线的buffer
        BufferedGraphics _gfxLineBuffer;
        
        // 全部区域的rect
        Rectangle _rect = new Rectangle();
        Point _cursorPoint = new Point();

        private List<graphicRegionView> _gRegionList = new List<graphicRegionView>();
        private List<dragBorder> _dBorderList = new List<dragBorder>();
        private bool _isDraggingBorder = false;
        private int _dragBorderIndex;

        private graphicRegionControl _regionControl = new graphicRegionControl();

        public graphicView()
        {
            constructGraphicRegion();
        }

        public void constructGraphicRegion()
        {
            // test code
            graphicRegionView region_one = new graphicRegionView("1");
            region_one.HeightRate = 0.33f;
            graphicRegionStatus regionModel_one = new graphicRegionStatus();
            region_one.setStatus(regionModel_one);
            _gRegionList.Add(region_one);

            graphicRegionView region_two = new graphicRegionView("2");
            region_two.HeightRate = 0.33f;
            graphicRegionStatus regionModel_two = new graphicRegionStatus();
            region_two.setStatus(regionModel_two);
            _gRegionList.Add(region_two);

            graphicRegionView region_three = new graphicRegionView("3");
            region_three.HeightRate = 0.33f;
            graphicRegionStatus regionModel_three = new graphicRegionStatus();
            region_three.setStatus(regionModel_three);
            _gRegionList.Add(region_three);

            _regionControl.setRegionViewList(_gRegionList);
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            paint();
        }

        private void paint()
        {
            _gfxBuffer.Graphics.Clear(Color.Black);
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                _gRegionList[i].CursorPoint = _cursorPoint; // !@!!!!@
                _gRegionList[i].onPaint(_gfxBuffer.Graphics, _gfxLineBuffer.Graphics);
            }
            drawDraggingLine(_gfxBuffer.Graphics);
            _gfxBuffer.Render();
        }

        protected override void OnResize(EventArgs e)
        {
            _gfx = this.CreateGraphics();
            _gfxBuffer = BufferedGraphicsManager.Current.Allocate(_gfx, this.DisplayRectangle);
            _gfxLineBuffer = BufferedGraphicsManager.Current.Allocate(_gfx, this.DisplayRectangle);

            // refresh the graphic region size
            _rect.Size = this.Size;
            _rect.Location = this.Location;
            _gfx.SetClip(_rect);

            resetRegionRect();

            this.Invalidate();
        }

        /// <summary>
        /// 重置
        /// </summary>
        private void resetRegionRect()
        {
            _dBorderList.Clear();
            float top = 0.0f;
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                RectangleF rect = new RectangleF();
                PointF location = new PointF();
                SizeF size = new SizeF();
                location.X = (float)_rect.Left;
                size.Width = (float)_rect.Width;

                location.Y = top;
                float height = _rect.Height * _gRegionList[i].HeightRate;
                size.Height = height;

                top += height;
                rect.Size = size;
                rect.Location = location;

                _gRegionList[i].Rect = rect;

                if (i > 0)
                {
                    dragBorder dborder = new dragBorder();
                    PointF dLocation = _gRegionList[i].Rect.Location;
                    SizeF dSize = _gRegionList[i].Rect.Size;
                    dLocation.Y -= 5;
                    dSize.Height = 10; // 10像素拖动区域
                    dborder._border = new RectangleF(dLocation, dSize);
                    dborder._upRegion = _gRegionList[i - 1];
                    dborder._downRegion = _gRegionList[i];
                    _dBorderList.Add(dborder);
                }
            }
        }

        private bool isInsideBorder(Point point, out int index)
        {
            index = -1;
            for (int i = 0; i < _dBorderList.Count; ++i)
            {
                if (_dBorderList[i]._border.Contains(point))
                {
                    index = i;
                    return true;
                }
            }
            return false;
        }


        private graphicRegionView findFocusedRegion(Point point)
        {
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                if (_gRegionList[i].isPointInside(point))
                    return _gRegionList[i];
            }
            return null;
        }

        protected override void OnMouseDown(MouseEventArgs e)
        {
            base.OnMouseDown(e);

            if (e.Button == MouseButtons.Right)
            {
                valueList vlist = new valueList();
                vlist.initSin();
                for (int i = 0; i < _gRegionList.Count; ++i)
                {
                    _gRegionList[i].TitleText = "Sin";
                    _gRegionList[i].addValueList(vlist);
                    _gRegionList[i].setShowRange(0, vlist.getLength());
                }
            }

            if (e.Button == MouseButtons.Left)
            {
                dragHandle(DRAG_MOUSE_STATUS.DOWN);
            }
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            _cursorPoint = e.Location;
            dragHandle(DRAG_MOUSE_STATUS.MOVE);
            paint();
        }

        protected override void OnMouseUp(MouseEventArgs e)
        {
            base.OnMouseUp(e);
            dragHandle(DRAG_MOUSE_STATUS.UP);
        }

        private void drawDraggingLine(Graphics gfx)
        {
            if (_isDraggingBorder)
            {
                Point p1 = new Point(_rect.Left, _cursorPoint.Y);
                Point p2 = new Point(_rect.Right, _cursorPoint.Y);
                gfx.DrawLine(_dragPen, p1, p2);
            }
        }

        /// <summary>
        /// 处理边界拖动事件
        /// </summary>
        private void dragHandle(DRAG_MOUSE_STATUS status)
        {
            if (status == DRAG_MOUSE_STATUS.MOVE)
            {
                if (_isDraggingBorder)
                {
                    Cursor.Current = Cursors.SizeNS;
                }
                else
                {
                    int index;
                    if (isInsideBorder(_cursorPoint, out index))
                    {
                        Cursor.Current = Cursors.SizeNS;
                    }
                }
            }
            else if (status == DRAG_MOUSE_STATUS.DOWN)
            {
                if (isInsideBorder(_cursorPoint, out _dragBorderIndex))
                {
                    _isDraggingBorder = true;
                    _regionControl.setCrossCursor(false);
                }
            }
            else if (status == DRAG_MOUSE_STATUS.UP)
            {
                if (_isDraggingBorder)
                {
                    _isDraggingBorder = false;
                    _regionControl.setCrossCursor(true);
                    dragBorder dBorder = _dBorderList[_dragBorderIndex];

                    // 超出范围
                    if (_cursorPoint.Y < dBorder._upRegion.Rect.Top ||
                        _cursorPoint.Y > dBorder._downRegion.Rect.Bottom)
                        return;

                    dBorder._border.Y = _cursorPoint.Y;
                    _dBorderList[_dragBorderIndex] = dBorder;

                    RectangleF rect = dBorder._upRegion.Rect;
                    SizeF size = dBorder._upRegion.Rect.Size;
                    size.Height = _cursorPoint.Y - dBorder._upRegion.Rect.Top;
                    rect.Size = size;
                    dBorder._upRegion.Rect = rect;

                    rect = dBorder._downRegion.Rect;
                    size = dBorder._downRegion.Rect.Size;
                    float preHeight = size.Height;
                    size.Height = dBorder._downRegion.Rect.Bottom - _cursorPoint.Y;
                    rect.Y = _cursorPoint.Y;
                    rect.Size = size;
                    dBorder._downRegion.Rect = rect;

                    paint();
                }
            }
            
        }

        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            switch (keyData)
            {
                case Keys.Left:
                    {
                        for (int i = 0; i < _gRegionList.Count; ++i)
                        {
                            _gRegionList[i].moveCursor(-1);
                        }
                        _cursorPoint = _gRegionList[0].CursorPoint;
                        paint();
                    }
                    break;
                case Keys.Right:
                    {
                        for (int i = 0; i < _gRegionList.Count; ++i)
                        {
                            _gRegionList[i].moveCursor(1);
                        }
                        _cursorPoint = _gRegionList[0].CursorPoint;
                        paint();
                    }
                    break;
                case Keys.Up:
                    break;
                case Keys.Down:
                    break;
            }
            return base.ProcessCmdKey(ref msg, keyData);
        }



    }
}
