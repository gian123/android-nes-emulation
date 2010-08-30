using System;
using System.Collections.Generic;
using System.Text;

namespace control_test
{
    class debuger
    {
        static public void trace(params object[] msg)
        {
            for (int i = 0; i < msg.Length; ++i)
            {
                System.Diagnostics.Debug.Write(msg[i].ToString() + " ");
            }

            System.Diagnostics.Debug.Write("\n");
        }
        
        static public void assert(bool condition)
        {
            System.Diagnostics.Debug.Assert(condition);
        }
        
    }
}
