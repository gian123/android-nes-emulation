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

        public int _curIndex = 0;
    }


    class graphicRegionStatus
    {
        /// <summary>
        /// ��֮������view
        /// </summary>
        private graphicRegionView _regionView;
        /// <summary>
        /// ���귴ת ���·�ת
        /// </summary>
        private bool _isCoordUDReversed;
        /// <summary>
        /// ���ҷ�ת
        /// </summary>
        private bool _isCoordLRReversed;
        /// <summary>
        /// �Ƿ����ʮ�ֹ��
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
