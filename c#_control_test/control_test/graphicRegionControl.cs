using System;
using System.Collections.Generic;
using System.Text;

namespace control_test
{
    class graphicRegionControl
    {
        private List<graphicRegionView> _gRegionList = null;

        public void setRegionViewList(List<graphicRegionView> list)
        {
            _gRegionList = list;
        }

        public void setCrossCursor(bool show)
        {
            if (_gRegionList == null)
                return;

            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                _gRegionList[i].getStatus().IsDrawCrossCursor = show;
            }
        }

    }


}
