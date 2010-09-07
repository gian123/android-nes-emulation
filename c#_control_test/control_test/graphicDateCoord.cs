using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

namespace control_test
{
    public struct xCoordDateInfo
    {
        /// <summary>
        /// 点id
        /// </summary>
        public int index;
        /// <summary>
        /// 显示时间文本的值(uint)形态
        /// </summary>
        public uint time;
    }

    public enum coordType
    {
        DAY,
        MIN,
    }

    class graphicDateCoord
    {
        private RectangleF _rect;

        private Graphics _gfxStatic;

        private Graphics _gfxDynamic;

        private Pen _borderPen = new Pen(Color.White);

        public RectangleF Rect
        {
            set { _rect = value; }
            get { return _rect; }
        }

        public void onPaint(Graphics gfxStatic, Graphics gfxDynamic)
        {
            _gfxStatic = gfxStatic;
            _gfxDynamic = gfxDynamic;
        }

        public void drawBorder()
        {
            PointF top_p1 = new PointF(_rect.Left, _rect.Top);
            PointF top_p2 = new PointF(_rect.Right, _rect.Top);
            _gfxStatic.DrawLine(_borderPen, top_p1, top_p2);

            PointF bottom_p1 = new PointF(_rect.Left, _rect.Bottom);
            PointF bottom_p2 = new PointF(_rect.Right, _rect.Bottom);
            _gfxStatic.DrawLine(_borderPen, bottom_p1, bottom_p2);

            PointF left_p1 = new PointF(_rect.Left, _rect.Top);
            PointF left_p2 = new PointF(_rect.Left, _rect.Bottom);
            _gfxStatic.DrawLine(_borderPen, left_p1, left_p2);

            PointF right_p1 = new PointF(_rect.Right, _rect.Top);
            PointF right_p2 = new PointF(_rect.Right, _rect.Bottom);
            _gfxStatic.DrawLine(_borderPen, right_p1, right_p2);
        }

        public void drawDate()
        {
            
        }
    }
}
