using System;
using System.Collections.Generic;
using System.Text;
using System.Drawing;

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

        public void setShowRange(int index, int beginIndex, int endIndex)
        {
            debuger.assert(index >= 0 && index < _gRegionList.Count);

            graphicRegionStatus status = _gRegionList[index].getStatus();
            status.setShowRange(beginIndex, endIndex);
        }


        public bool moveCursor(int step)
        {
            bool needPaintAll = false;
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                needPaintAll = _gRegionList[i].moveCursor(step);
            }
            return needPaintAll;
        }

        public bool scaleRange(bool isUp)
        {
            bool needPaintAll = false;
            for (int i = 0; i < _gRegionList.Count; ++i)
            {
                needPaintAll = _gRegionList[i].scaleRange(isUp);
            }
            return needPaintAll;
        }

        public void setGfx(Graphics gfx, Graphics gfxLines)
        {
            for (int i = 0; i < _gRegionList.Count; ++i)
                _gRegionList[i].setGfx(gfx, gfxLines);
        }



    }


}
