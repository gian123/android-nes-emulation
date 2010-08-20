using System;
using System.Collections.Generic;
using System.Text;

namespace control_test
{
    class graphicRegionModel
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

        private List<valueList> _vList = new List<valueList>();

        public delegate void valueChanged();
        public event valueChanged onValueChanged;

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


    }


}
