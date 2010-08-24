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

        Graphics _gfx;
        BufferedGraphics _gfxBuffer;
        
        // 全部区域的rect
        Rectangle _rect = new Rectangle();
        Point _cursorPoint = new Point();

        private List<graphicRegionView> _gRegionList = new List<graphicRegionView>();
        private List<dragBorder> _dBorderList = new List<dragBorder>();

        public graphicView()
        {
            constructGraphicRegion();
        }

        public void constructGraphicRegion()
        {
            // test code
            graphicRegionView region_one = new graphicRegionView("1");
            region_one.HeightRate = 0.5f;
            graphicRegionModel regionModel_one = new graphicRegionModel();
            region_one.setModel(regionModel_one);
            _gRegionList.Add(region_one);

            graphicRegionView region_two = new graphicRegionView("2");
            region_two.HeightRate = 0.5f;
            graphicRegionModel regionModel_two = new graphicRegionModel();
            region_two.setModel(regionModel_two);
            _gRegionList.Add(region_two);
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
                _gRegionList[i].onPaint(_gfxBuffer.Graphics);
            }
            _gfxBuffer.Render();
        }

        protected override void OnResize(EventArgs e)
        {
            _gfx = this.CreateGraphics();
            _gfxBuffer = BufferedGraphicsManager.Current.Allocate(_gfx, this.DisplayRectangle);

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
                graphicRegionView focusedRegion = findFocusedRegion(e.Location);
                MessageBox.Show(focusedRegion.getName());
            }
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            _cursorPoint = e.Location;
            int index;
            if (isInsideBorder(_cursorPoint, out index))
            {
                Cursor.Current = Cursors.SizeNS;
            }
            paint();
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
