using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

namespace control_test
{

    class graphicRegionView
    {
        // for debug
        private string _regionName = null;

        /// <summary>
        /// region rect
        /// </summary>
        private RectangleF _rect;
        /// <summary>
        /// ͼ������
        /// </summary>
        private RectangleF _gfxRect;

        //private RectangleF _parentRect;

        public const float MIN_HEIGHT = 30;

        private String _titleText;
        /// <summary>
        /// ������ǩ�����
        /// </summary>
        private int _valueLabelWidth = 50;
        private int _titleTxtHeight = 10;

        private Pen _borderPen = new Pen(Color.White);
        private Pen _borderPen2 = new Pen(Color.White);

        private Pen _cursorPen = new Pen(Color.Blue);
        private Font _defaultFont = new Font("system", 10, FontStyle.Regular);
        private SolidBrush _fontBrush = new SolidBrush(Color.Green);

        private Color _bkColor = Color.Black;
        /// <summary>
        /// ����ʮ�ֹ�꣬������ǩ����ʱ�ڱ仯��ͼ��
        /// </summary>
        private Graphics _gfx = null;
        /// <summary>
        /// �����������ߣ�������Ϣ�Ȳ����ױ仯��ͼ��
        /// </summary>
        private Graphics _gfxLine = null;

        private Point _cursorPoint = new Point();
        // ������ռ������ͼ����İٷֱ�
        // _rect.Height = parent's Height * _heightRate;
        private float _heightRate = 1.0f;

        private regionValueStatus _vStatues = null;

        private graphicRegionStatus _status = null;

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
            set 
            { 
                _rect = value;
                PointF location = _rect.Location;
                SizeF size = _rect.Size;

                _titleTxtHeight = _defaultFont.Height;
                location.X += (float)_valueLabelWidth;
                location.Y += (float)_titleTxtHeight;
                size.Width -= (float)_valueLabelWidth;
                size.Height -= (float)_titleTxtHeight;

                _gfxRect.Location = location;
                _gfxRect.Size = size;
            }

            get { return _rect; }
        }

        public RectangleF GfxRect
        {
            get { return _gfxRect; }
        }

        public Point CursorPoint
        {
            set 
            {
                _cursorPoint = value;
                _vStatues._curIndex = getXIndex(_cursorPoint);
            }
            get { return _cursorPoint; }
        }

        public String TitleText
        {
            set { _titleText = value; }
        }

        public graphicRegionView()
        {
            _regionName = "graphicRegionView";
        }

        public graphicRegionView(string name)
        {
            _regionName = name;
        }

        public string getName()
        {
            return _regionName;
        }

        public void setStatus(graphicRegionStatus status)
        {
            _status = status;
            _vStatues = status.ValueStatus;

            _status.onValueChanged += new graphicRegionStatus.valueChanged(paintAll);
        }

        public graphicRegionStatus getStatus()
        {
            return _status;
        }

        public void addValueList(valueList list)
        {
            if (_status != null)
                _status.addValueList(list);
        }

        public void setTagDic(Dictionary<int, INFO_TAG> tagDic)
        {
            _status.setTagDic(tagDic);
        }


        public void onPaint()
        {
            if (_status.IsDrawCrossCursor)
                drawCrossCursorAndLabel();
            drawTitle();
            drawTag();
        }

        public void setGfx(Graphics gfx, Graphics gfxLine)
        {
            _gfx = gfx;
            _gfxLine = gfxLine;
        }

        public void paintAll()
        {
            if (_gfx == null)
                return;

            filterValues(); // ���Ƴ����ú���
            drawBorder();
            drawLines();
        }

        private void drawBorder()
        {
            PointF top_p1 = new PointF(_rect.Left, _rect.Top);
            PointF top_p2 = new PointF(_rect.Right, _rect.Top);
            _gfxLine.DrawLine(_borderPen, top_p1, top_p2);

            PointF vertical_p1 = new PointF(_gfxRect.Left, _rect.Top);
            PointF vertical_p2 = new PointF(_gfxRect.Left, _rect.Bottom);
            _gfxLine.DrawLine(_borderPen, vertical_p1, vertical_p2);

            float halfHeight = _rect.Height / 2;
            PointF middle_p1 = new PointF(_gfxRect.Left, _gfxRect.Bottom - halfHeight);
            PointF middle_p2 = new PointF(_gfxRect.Right, _gfxRect.Bottom - halfHeight);
            _gfxLine.DrawLine(_borderPen, middle_p1, middle_p2);
        }

        /// <summary>
        /// ����ֵ�ĸ�����ǩ
        /// </summary>
        private void drawValueLabel()
        {
            int labelHalfHeight = 10; // ��ʱ��Ϊ10���ܸ߶�Ϊ20
            Rectangle rect = new Rectangle((int)_rect.Left, (int)(_cursorPoint.Y - labelHalfHeight), 
                                            _valueLabelWidth, labelHalfHeight * 2);

            float value = (_gfxRect.Bottom - _cursorPoint.Y) / _vStatues._YRate + _vStatues._minValue;
            _gfx.DrawRectangle(_borderPen, rect);
            _gfx.DrawString(value.ToString("0.00"), _defaultFont, _fontBrush, rect.Location);

        }

        /// <summary>
        /// ����ʮ�ֹ��
        /// </summary>
        private void drawCrossCursorAndLabel()
        {
            // ����, �����ڸ������ڲŻ��ƺ���
            if (isPointInside(_cursorPoint))
            {
                Point left = new Point((int)_gfxRect.Left, _cursorPoint.Y);
                Point right = new Point((int)_gfxRect.Right, _cursorPoint.Y);
                _gfx.DrawLine(_cursorPen, left, right);

                if (_cursorPoint.Y >= _gfxRect.Top)
                    drawValueLabel();
            }
            // ����
            Point up = new Point(_cursorPoint.X, (int)_gfxRect.Top);
            Point down = new Point(_cursorPoint.X, (int)_gfxRect.Bottom);
            _gfx.DrawLine(_cursorPen, up, down);
        }

        private void drawTitle()
        {
            string title = _titleText + " ";

            List<valueList> vList = _status.getValueList(); 
            for (int i = 0; i < vList.Count; ++i)
            {
                title += vList[i].getValueStr(_vStatues._curIndex);
            }
            _gfx.DrawString(title, _defaultFont, _fontBrush, _gfxRect.X, _gfxRect.Top - _titleTxtHeight);
        }

        /// <summary>
        /// find the min max value 
        /// and the height rate
        /// ..to do : Exception handling
        /// </summary>
        private void filterValues()
        {
            debuger.assert(_status != null);

            if (_status == null)
                return;

            _vStatues._minValue = float.MaxValue;
            _vStatues._maxValue = float.MinValue;

            List<valueList> vList = _status.getValueList();

            float tmpMin, tmpMax;
            for (int i = 0; i < vList.Count; ++i)
            {
                vList[i].findMinMax(_vStatues._showBeginIndex, _vStatues._showEndIndex, out tmpMin, out tmpMax);
                if (_vStatues._minValue > tmpMin)
                    _vStatues._minValue = tmpMin;
                if (_vStatues._maxValue < tmpMax)
                    _vStatues._maxValue = tmpMax;
            }

            _vStatues._YRate = _gfxRect.Height / (_vStatues._maxValue - _vStatues._minValue);
            int showDistance = _vStatues._showEndIndex - _vStatues._showBeginIndex;
            if (showDistance < _vStatues._fixedDisplayDistance)
                showDistance = _vStatues._fixedDisplayDistance;
            _vStatues._XRate = _gfxRect.Width  / showDistance;
        }

        private void drawLines()
        {
            List<valueList> vList = _status.getValueList();

            for (int i = 0; i < vList.Count; ++i)
            {
                valueList lineValues = vList[i];
                PointF prePoint = new PointF();
                PointF curPoint = new PointF();

                for (int j = 0; lineValues.values[j] != null; ++j)
                {
                    debuger.assert(_vStatues._showBeginIndex >= 0 && 
                                   _vStatues._showEndIndex <= lineValues.values[j].Length &&
                                   _vStatues._showEndIndex > _vStatues._showBeginIndex);

                    float[] valueArray = lineValues.values[j];
                    for (int m = _vStatues._showBeginIndex; m < _vStatues._showEndIndex; ++m)
                    {
                        float x = getXPos(m);
                        float y = getYPos(valueArray[m]); //_gfxRect.Bottom - (valueArray[m] - _vStatues._minValue) * _vStatues._YRate;

                        if (m == _vStatues._showBeginIndex)
                        {
                            prePoint.X = x;
                            prePoint.Y = y;
                            continue;
                        }

                        curPoint.X = x;
                        curPoint.Y = y;

                        _gfxLine.DrawLine(_borderPen, prePoint, curPoint);

                        prePoint = curPoint;
                    }
                }
                
            }
        }

        private void drawTag()
        {
            Dictionary<int, INFO_TAG> tagDic = _status.getTagDic();
            if (tagDic == null)
                return;

            foreach (KeyValuePair<int, INFO_TAG> var in tagDic)
            {
                if (var.Key <= _vStatues._showBeginIndex || var.Key >= _vStatues._showEndIndex)
                    continue;

                float x = getXPos(var.Key);
                _gfx.DrawString("��", _defaultFont, _fontBrush, x, _gfxRect.Top);
            }
        }

        /// <summary>
        /// �ƶ�ʮ�ֹ��
        /// </summary>
        /// <returns>�Ƿ���Ҫȫ���ػ�</returns>
        public bool moveCursor(int step)
        {
            if (!checkRange())
                return false;

            int tmpIndex = _vStatues._curIndex;
            tmpIndex += step;

            List<valueList> vList = _status.getValueList();

            if (tmpIndex >= _vStatues._showBeginIndex && tmpIndex < _vStatues._showEndIndex)
            {
                _vStatues._curIndex = tmpIndex;
                _cursorPoint.X = (int)getXPos(_vStatues._curIndex);
                return false;
            }
            else if (tmpIndex >= 0 && tmpIndex < vList[0].getLength())
            {
                _vStatues._showBeginIndex += step;
                _vStatues._showEndIndex += step;
                _vStatues._curIndex = tmpIndex;
                return true;
            }

            return false;
        }

        /// <summary>
        /// ��Χ����
        /// </summary>
        /// <param name="up">�Ƿ�Ϊ�Ŵ�,����Ϊ��С</param>
        /// <returns>�Ƿ���Ҫȫ���ػ�</returns>
        public bool scaleRange(bool up)
        {
            if (!checkRange())
                return false;

            int step = (_vStatues._showEndIndex - _vStatues._showBeginIndex) * 3 / 10; // 30%��������
            step = (step != 0) ? step : 1;
            
            if (up)
            {
                int tmpBeginIndex = _vStatues._showBeginIndex + step;
                if (_vStatues._showEndIndex - tmpBeginIndex >= 10)
                {
                    _vStatues._showBeginIndex = tmpBeginIndex;
                    return true;
                }
            }
            else // down
            {
                List<valueList> vList = _status.getValueList();
                int length = vList[0].getLength();

                // �����С
                if (_vStatues._showBeginIndex == 0 && _vStatues._showEndIndex < length)
                {
                    int tmpEndIndex = _vStatues._showEndIndex + step;
                    _vStatues._showEndIndex = (tmpEndIndex > length) ? length : tmpEndIndex;
                    return true;
                }
                // ������С
                else if (_vStatues._showBeginIndex > 0)
                {
                    int tmpBeginIndex = _vStatues._showBeginIndex - step;
                    _vStatues._showBeginIndex = (tmpBeginIndex < 0) ? 0 : tmpBeginIndex;
                    return true;
                }
            }

            return false;
        }


        /// <summary>
        /// һ���Ƿ��ڸ�������
        /// </summary>
        public bool isPointInside(Point point)
        {
            return _rect.Contains((float)point.X, (float)point.Y);
        }

        public int getXIndex(Point point)
        {
            return getXIndex(point.X);
        }

        public int getXIndex(int xPos)
        {
            int index = (int)((xPos - _gfxRect.Left) / _vStatues._XRate) + 
                               _vStatues._showBeginIndex;
            if (index < 0)
                return 0;
            return index;
        }

        /// <summary>
        /// ����x��������x����λ��
        /// </summary>
        private float getXPos(int xIndex)
        {
            xIndex -= _vStatues._showBeginIndex;
            float pos = xIndex * _vStatues._XRate + _gfxRect.Left + _vStatues._XRate / 2;
            return pos;
        }

        private float getYPos(float value)
        {
            float y = _gfxRect.Bottom - (value - _vStatues._minValue) * _vStatues._YRate;
            return y;
        }

        private bool checkRange()
        {
            List<valueList> vList = _status.getValueList();
            return vList.Count > 0;
        }

        /// <summary>
        /// ���Ƿ���������
        /// </summary>
        public bool isPointAtLines(Point point)
        {
            if (!isPointInside(point))
                return false;
            if (!checkRange())
                return false;

            List<valueList> vList = _status.getValueList();
            for (int i = 0; i < vList.Count; ++i)
            {
                float[][] values = vList[i].values;
                for (int j = 0; j < values.Length && values[j] != null; ++j)
                {
                    float value = values[j][_vStatues._curIndex];
                    float yPos = getYPos(value);
                    float yTop = yPos - 2;
                    float yBottom = yPos + 2;
                    if (point.Y > yTop && point.Y < yBottom)
                        return true;
                }
            }

            return false;
        }


    }
}
