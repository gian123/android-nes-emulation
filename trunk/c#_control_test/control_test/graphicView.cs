using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;
using System.Windows.Forms;

namespace control_test
{
    class graphicView : Control
    {
        Graphics _gfx;
        BufferedGraphics _gfxBuffer;
        
        // 全部区域的rect
        Rectangle _gfxRect = new Rectangle();
        Point _cursorPoint = new Point();


        private List<graphicRegionView> _gRegionList = new List<graphicRegionView>();

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
            _gfxRect.Size = this.Size;
            _gfxRect.Location = this.Location;
            _gfx.SetClip(_gfxRect);
    
            float top = 0.0f;
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                RectangleF rect = new RectangleF();
                PointF location = new PointF();
                SizeF size = new SizeF();
                location.X = (float)_gfxRect.Left;
                size.Width = (float)_gfxRect.Width;

                location.Y = top;
                float height = _gfxRect.Height * _gRegionList[i].HeightRate;
                size.Height = height;

                top += height;
                rect.Size = size;
                rect.Location = location;

                _gRegionList[i].Rect = rect;
            }

            this.Invalidate();
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

            if (e.Button == MouseButtons.Left)
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

            if (e.Button == MouseButtons.Right)
            {
                graphicRegionView focusedRegion = findFocusedRegion(e.Location);
                MessageBox.Show(focusedRegion.getName());
            }
        }

        protected override void OnMouseMove(MouseEventArgs e)
        {
            base.OnMouseMove(e);
            _cursorPoint = e.Location;
            paint();
            //drawCrossCursor(_gfxBuffer.Graphics, e.Location);
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
                        paint();
                    }
                    break;
                case Keys.Right:
                    {
                        for (int i = 0; i < _gRegionList.Count; ++i)
                        {
                            _gRegionList[i].moveCursor(1);
                        }
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
