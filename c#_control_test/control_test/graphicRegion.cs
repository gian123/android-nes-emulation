using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

namespace control_test
{
    class regionValueStatus
    {
        public float _minValue = 0.0f;
        public float _maxValue = 0.0f;
        // 数据绘制的Y值 = value * _YRate + _rect.Bottom
        public float _YRate;
        // 数据绘制的X值 = index * _XRate + _rect.Left
        public float _XRate;

        public int showBeginIndex = 0;
        public int showEndIndex = 0;

        public List<valueList> _vList = new List<valueList>();
    }

    struct regionControlStatus
    {
        /// <summary>
        /// 坐标反转 上下翻转
        /// </summary>
        bool _isCoordUDReversed;
        /// <summary>
        /// 左右翻转
        /// </summary>
        bool _isCoordLRReversed;
    }

    class graphicRegion
    {
        /// <summary>
        /// region rect
        /// </summary>
        private RectangleF _rect;
        private Pen _borderPen = new Pen(Color.White);
        private Graphics _gfx;

        // 该区域占整个绘图区域的百分比
        // _rect.Height = parent's Height * _heightRate;
        private float _heightRate = 1.0f;

        private regionValueStatus _vStatues = new regionValueStatus();        

        public float HeightRate
        {
            set 
            {
                if (0.0f > value || value > 1.0f)
                    return;
                _heightRate = value;
            }
            get { return _heightRate; }
        }

        public RectangleF Rect
        {
            set { _rect = value; }
            get { return _rect; }
        }


        public void addValueList(valueList list)
        {
            _vStatues._vList.Add(list);
        }

        public void setShowRange(int beginIndex, int endIndex)
        {
            _vStatues.showBeginIndex = beginIndex;
            _vStatues.showEndIndex = endIndex;
        }

        public void onPaint(Graphics gfx)
        {
            _gfx = gfx;
            
            filterValues();
            drawBorder();
            drawLines();
        }

        private void drawBorder()
        {
            float top = _rect.Top + 10;
            PointF top_p1 = new PointF(_rect.Left, top);
            PointF top_p2 = new PointF(_rect.Right, top);
            _gfx.DrawLine(_borderPen, top_p1, top_p2);

            PointF bottom_p1 = new PointF(_rect.Left, _rect.Bottom - 10);
            PointF bottom_p2 = new PointF(_rect.Right, _rect.Bottom - 10);
            _gfx.DrawLine(_borderPen, bottom_p1, bottom_p2);

            //debuger.trace((bottom_p2.X - bottom_p1.X).ToString());
        }

        /// <summary>
        /// find the min max value 
        /// and the height rate
        /// ..to do : Exception handling
        /// </summary>
        private void filterValues()
        {
            _vStatues._minValue = float.MaxValue;
            _vStatues._maxValue = float.MinValue;
            float tmpMin, tmpMax;
            for (int i = 0; i < _vStatues._vList.Count; ++i)
            {
                _vStatues._vList[i].findMinMax(_vStatues.showBeginIndex, _vStatues.showEndIndex, out tmpMin, out tmpMax);
                if (_vStatues._minValue > tmpMin)
                    _vStatues._minValue = tmpMin;
                if (_vStatues._maxValue < tmpMax)
                    _vStatues._maxValue = tmpMax;
            }

            _vStatues._YRate = _rect.Height / (_vStatues._maxValue - _vStatues._minValue);
            _vStatues._XRate = _rect.Width / (_vStatues.showEndIndex - _vStatues.showBeginIndex);
        }

        private void drawLines()
        {
            for (int i = 0; i < _vStatues._vList.Count; ++i)
            {
                valueList lineValues = _vStatues._vList[i];
                PointF prePoint = new PointF();
                PointF curPoint = new PointF();

                for (int j = 0; lineValues.values[j] != null; ++j)
                {
                    debuger.assert(_vStatues.showBeginIndex >= 0 && 
                                   _vStatues.showEndIndex <= lineValues.values[j].Length &&
                                   _vStatues.showEndIndex > _vStatues.showBeginIndex);

                    float[] valueArray = lineValues.values[j];
                    for (int m = _vStatues.showBeginIndex, n = 0; m < _vStatues.showEndIndex; ++m, ++n)
                    {
                        float x = n * _vStatues._XRate + _rect.Left;
                        float y = (valueArray[m] - _vStatues._minValue) * _vStatues._YRate + _rect.Top;

                        if (n == 0)
                        {
                            prePoint.X = x;
                            prePoint.Y = y;
                            continue;
                        }

                        curPoint.X = x;
                        curPoint.Y = y;

                        _gfx.DrawLine(_borderPen, prePoint, curPoint);

                        prePoint = curPoint;
                    }
                }
                
            }
        }




    }
}
