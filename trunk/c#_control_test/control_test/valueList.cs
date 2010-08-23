using System;
using System.Collections.Generic;
using System.Text;

namespace control_test
{
    class valueList
    {
        public float[][] values = new float[16][];
        public float _min = float.MaxValue;
        public float _max = float.MinValue;

        public void initSin()
        {
            const float pi = 3.1415926f;
            
            values[0] = new float[50];
            float step = 4 * pi / values[0].Length;

            for (int i = 0; i < values[0].Length; ++i)
            {
                values[0][i] = (float)Math.Sin(i * step);
            }
        }

        public void findMinMax(int beginIndex, int endIndex, out float min, out float max)
        {
            min = float.MaxValue;
            max = float.MinValue;
            for (int i = 0; i < values.Length && values[i] != null; ++i)
            {
                
                for (int j = beginIndex; j < endIndex; ++j)
                {
                    if (values[i][j] > max)
                        max = values[i][j];
                    if (values[i][j] < min)
                        min = values[i][j];
                }
            }
            _min = min;
            _max = max;
        }

        public int getLength()
        {
            if (values[0] == null)
                return 0;
            return values[0].Length;
        }

        public String getValueStr(int index)
        {
            String str = "";
            for (int i = 0; i < values.Length && values[i] != null; ++i)
            {
                debuger.assert(index >= 0 && index < values[i].Length);
                str += values[i][index].ToString("0.00");
                str += " ";
            }
            return str;
        }
    }

}
