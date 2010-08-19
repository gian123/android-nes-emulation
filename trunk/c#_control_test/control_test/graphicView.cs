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
        
        Rectangle _gfxRect = new Rectangle();

        private List<graphicRegion> _gRegionList = new List<graphicRegion>();

        public graphicView()
        {
            _gfx = this.CreateGraphics();
            _gfxBuffer = BufferedGraphicsManager.Current.Allocate(_gfx, this.DisplayRectangle);
            constructGraphicRegion();
        }

        public void constructGraphicRegion()
        {
            // test code
            graphicRegion region_one = new graphicRegion();
            region_one.HeightRate = 1.0f;
            _gRegionList.Add(region_one);

            valueList vlist = new valueList();
            vlist.initSin();
            region_one.addValueList(vlist);
            region_one.setShowRange(0, vlist.getLength());

            //graphicRegion region_two = new graphicRegion();
            //region_two.HeightRate = 0.5f;
            //_gRegionList.Add(region_two);
        }

        protected override void OnPaint(PaintEventArgs e)
        {
            Graphics gfx = e.Graphics;
            gfx.Clear(Color.Black);
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                _gRegionList[i].onPaint(gfx);
            }
        }

        protected override void OnResize(EventArgs e)
        {
            // refresh the graphic region size
            _gfxRect.Size = this.Size;
            _gfxRect.Location = this.Location;
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


        

    }
}
