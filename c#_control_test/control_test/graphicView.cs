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

        private Color _bkColor = Color.Black;

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

        private graphicDateCoord _gDateCoord = new graphicDateCoord();
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
            _gfxBuffer.Graphics.Clear(_bkColor);
            _gfxLineBuffer.Render(_gfxBuffer.Graphics);
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                _gRegionList[i].CursorPoint = _cursorPoint;
                _gRegionList[i].onPaint();
            }

            _gDateCoord.onPaint();

            drawDraggingLine(_gfxBuffer.Graphics);
            _gfxBuffer.Render();
        }

        private void paintAll()
        {
            // 画线
            _gfxBuffer.Graphics.Clear(_bkColor);
            _gfxLineBuffer.Graphics.Clear(_bkColor);
            
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                _gRegionList[i].CursorPoint = _cursorPoint;
                _gRegionList[i].paintAll();
            }
            _gDateCoord.paintAll();

            // 绘制十字光标，标题等
            paint();
        }

        protected override void OnResize(EventArgs e)
        {
            _gfx = this.CreateGraphics();
            _gfxBuffer = BufferedGraphicsManager.Current.Allocate(_gfx, this.DisplayRectangle);
            _gfxLineBuffer = BufferedGraphicsManager.Current.Allocate(_gfx, this.DisplayRectangle);

            _regionControl.setGfx(_gfxBuffer.Graphics, _gfxLineBuffer.Graphics);
            _gDateCoord.setGfx(_gfxBuffer.Graphics, _gfxLineBuffer.Graphics);

            // refresh the graphic region size
            _rect.Size = this.Size;
            _rect.Location = this.Location;
            _gfx.SetClip(_rect);

            resetRegionRect();
            paintAll();
        }

        /// <summary>
        /// 重置
        /// </summary>
        private void resetRegionRect()
        {
            _dBorderList.Clear();
            float top = 0.0f;
            const int dateCoordHeight = 20;
            int regionHeight = _rect.Height - dateCoordHeight;

            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                RectangleF rect = new RectangleF();
                PointF location = new PointF();
                SizeF size = new SizeF();
                location.X = (float)_rect.Left;
                size.Width = (float)_rect.Width;

                location.Y = top;
                float height = regionHeight * _gRegionList[i].HeightRate;
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
            // 日期标尺
            int leftBlankWidth = _gRegionList[0].ValueLableWidth;
            RectangleF dcRect = new RectangleF();
            PointF dcLocation = new PointF();
            SizeF dcSize = new SizeF();
            dcLocation.X = _rect.X + leftBlankWidth;
            dcLocation.Y = _rect.Bottom - dateCoordHeight;
            dcSize.Width = _rect.Width - leftBlankWidth;
            dcSize.Height = dateCoordHeight;
            dcRect.Location = dcLocation;
            dcRect.Size = dcSize;
            _gDateCoord.Rect = dcRect;
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
                valueList vlist2 = new valueList();
                vlist2.initRandom();
                vlist.initSin();
                for (int i = 0; i < _gRegionList.Count; ++i)
                {
                    _gRegionList[i].TitleText = "Sin";
                    _regionControl.setShowRange(i, 0, vlist.getLength() - 50);
                    _gRegionList[i].addValueList(vlist);
                    _gRegionList[i].addValueList(vlist2);
                }

                Dictionary<int, INFO_TAG> tagDic = new Dictionary<int, INFO_TAG>();
                tagDic.Add(30, INFO_TAG.DAY_INFO_MINE);
                tagDic.Add(60, INFO_TAG.DAY_INFO_MINE);
                _gRegionList[0].setTagDic(tagDic);
                paintAll();
            }

            if (e.Button == MouseButtons.Left)
            {
                dragHandle(DRAG_MOUSE_STATUS.DOWN);
            }
        }

        /// <summary>
        /// 防止OnMouseMove一直被触发
        /// </summary>
        private Point _lastPoint = new Point();

        protected override void OnMouseMove(MouseEventArgs e)
        {
            if (_lastPoint == e.Location)
                return;

            _lastPoint = e.Location;
            _cursorPoint = e.Location;

            dragHandle(DRAG_MOUSE_STATUS.MOVE);
            paint();
            ResetMouseEventArgs();

            base.OnMouseMove(e);
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

                    paintAll();
                }
            }
        }

        protected override void OnMouseHover(EventArgs e)
        {
            Point point = Cursor.Position;
            point = this.PointToClient(point);
            bool hit = false;
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                if (_gRegionList[i].isPointAtLines(point))
                {
                    hit = true;
                    break;
                }
            }
        }

        protected override bool ProcessCmdKey(ref Message msg, Keys keyData)
        {
            switch (keyData)
            {
                case Keys.Left:
                case Keys.Right:
                    {
                        int step = 0;
                        if (keyData == Keys.Left)
                            step = -1;
                        else
                            step = 1;

                        bool needPaintAll = _regionControl.moveCursor(step);
                        _cursorPoint = _gRegionList[0].CursorPoint;
                        if (needPaintAll)
                            paintAll();
                        else
                            paint();
                    }
                    break;
                case Keys.Up:
                case Keys.Down:
                    {
                        bool isUp = false;
                        if (keyData == Keys.Up)
                            isUp = true;

                        bool needPaintAll = _regionControl.scaleRange(isUp);
                        if (needPaintAll)
                            paintAll();
                        else
                            paint();
                    }
                    break;
            }
            return base.ProcessCmdKey(ref msg, keyData);
        }



    }
}
