using System;
using System.Collections.Generic;
using System.Text;

namespace control_test
{
    enum INFO_TAG
    {
        DAY_INFO_MINE,
        DAY_WEIGHT,
        MIN_INFO_MINE,
    }

    class regionValueStatus
    {
        public float _minValue = 0.0f;
        public float _maxValue = 0.0f;
        // 数据绘制的Y值 = _gfxRect.Bottom - value * _YRate
        public float _YRate;
        // 数据绘制的X值 = index * _XRate + _gfxRect.Left
        public float _XRate;

        public int _showBeginIndex = 0;
        public int _showEndIndex = 0;
        /// <summary>
        /// 固定显示的范围（如分时线固定显示241条的范围）
        /// </summary>
        public int _fixedDisplayDistance = 0;

        public int _curIndex = 0;
    }


    class graphicRegionStatus
    {
        /// <summary>
        /// 与之关联的view
        /// </summary>
        private graphicRegionView _regionView;
        /// <summary>
        /// 坐标反转 上下翻转
        /// </summary>
        private bool _isCoordUDReversed;
        /// <summary>
        /// 左右翻转
        /// </summary>
        private bool _isCoordLRReversed;
        /// <summary>
        /// 是否绘制十字光标
        /// </summary>
        private bool _isDrawCrossCursor = true;

        private List<valueList> _vList = new List<valueList>();

        private Dictionary<int, INFO_TAG> _tagDic = null;

        private regionValueStatus _vStatus = new regionValueStatus();

        public delegate void valueChanged();
        public event valueChanged onValueChanged;

        public bool IsDrawCrossCursor
        {
            get { return _isDrawCrossCursor; }
            set { _isDrawCrossCursor = value; }
        }

        public regionValueStatus ValueStatus
        {
            get { return _vStatus; }
            set { _vStatus = value; }
        }

        public List<valueList> getValueList()
        {
            return _vList;
        }

        public void addValueList(valueList values)
        {
            _vList.Add(values);
            if (onValueChanged != null)
                onValueChanged();
        }

        public void setTagDic(Dictionary<int, INFO_TAG> tagDic)
        {
            _tagDic = tagDic;
        }

        public Dictionary<int, INFO_TAG> getTagDic()
        {
            return _tagDic;
        }

        public void setShowRange(int beginIndex, int endIndex)
        {
            _vStatus._showBeginIndex = beginIndex;
            _vStatus._showEndIndex = endIndex;
        }

        public void getShowRange(out int beginIndex, out int endIndex)
        {
            beginIndex = _vStatus._showBeginIndex;
            endIndex = _vStatus._showEndIndex;
        }


    }


}
