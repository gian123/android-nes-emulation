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
        // ���ݻ��Ƶ�Yֵ = _gfxRect.Bottom - value * _YRate
        public float _YRate;
        // ���ݻ��Ƶ�Xֵ = index * _XRate + _gfxRect.Left
        public float _XRate;

        public int _showBeginIndex = 0;
        public int _showEndIndex = 0;
        /// <summary>
        /// �̶���ʾ�ķ�Χ�����ʱ�߹̶���ʾ241���ķ�Χ��
        /// </summary>
        public int _fixedDisplayDistance = 0;
        //public List<valueList> _vList = new List<valueList>();
    }


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
        /// ������ǩ������
        /// </summary>
        private int _valueLabelWidth = 50;
        private int _titleTxtHeight = 10;

        private Pen _borderPen = new Pen(Color.White);
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

        private regionValueStatus _vStatues = new regionValueStatus();

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
            set { _cursorPoint = value; }
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
            _status.onValueChanged += new graphicRegionStatus.valueChanged(filterValues);
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

        public void setShowRange(int beginIndex, int endIndex)
        {
            _vStatues._showBeginIndex = beginIndex;
            _vStatues._showEndIndex = endIndex;
        }

        public void getShowRange(out int beginIndex, out int endIndex)
        {
            beginIndex = _vStatues._showBeginIndex;
            endIndex = _vStatues._showEndIndex;
        }

        public void onPaint(Graphics gfx, Graphics gfxLine)
        {
            _gfx = gfx;
            _gfxLine = gfxLine;
            paintAll();
        }


        public void paintAll()
        {
            if (_gfx == null)
                return;

            filterValues(); // ���Ƴ����ú���
            drawBorder();
            if (_status.IsDrawCrossCursor)
                drawCrossCursorAndLabel();
            drawTitle();
            drawTag();
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
            int index = getXIndex(_cursorPoint);
            List<valueList> vList = _status.getValueList(); 
            for (int i = 0; i < vList.Count; ++i)
            {
                title += vList[i].getValueStr(index);
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
            //_gfxLine.Clear(_bkColor);

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
                    for (int m = _vStatues._showBeginIndex, n = 0; m < _vStatues._showEndIndex; ++m, ++n)
                    {
                        float x = n * _vStatues._XRate + _gfxRect.Left;
                        float y = _gfxRect.Bottom - (valueArray[m] - _vStatues._minValue) * _vStatues._YRate;

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

        private void drawTag()
        {
            Dictionary<int, INFO_TAG> tagDic = _status.getTagDic();
            if (tagDic == null)
                return;

            foreach (KeyValuePair<int, INFO_TAG> var in tagDic)
            {
                if (var.Key <= _vStatues._showBeginIndex || var.Key >= _vStatues._showEndIndex)
                    continue;

                float x = var.Key * _vStatues._XRate + _gfxRect.Left;
                _gfx.DrawString("��", _defaultFont, _fontBrush, x, _gfxRect.Top);
            }
        }

        public void moveCursor(int step)
        {
            float tmpX = (float)_cursorPoint.X;
            tmpX += step * _vStatues._XRate;

            // ��ʾ��������
            if (tmpX < _gfxRect.Left)
            {
                if (_vStatues._showBeginIndex + step >= 0)
                {
                    _vStatues._showBeginIndex += step;
                    _vStatues._showEndIndex += step;
                }
                else
                    return;
            }

            // ��ʾ��������
            else if (tmpX > _gfxRect.Right)
            {
                List<valueList> vList = _status.getValueList();
                if (_vStatues._showEndIndex + step <= vList[0].getLength())
                {
                    _vStatues._showBeginIndex += step;
                    _vStatues._showEndIndex += step;
                }
                else
                    return;
            }
            else
                _cursorPoint.X = (int)tmpX;
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

        public int getXIndex(int x)
        {
            int index = (int)((x - _gfxRect.Left) / _vStatues._XRate);
            if (index < 0)
                return 0;
            return index;
        }


    }
}