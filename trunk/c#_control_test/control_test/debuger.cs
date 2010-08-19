using System;
using System.Collections.Generic;
using System.Text;

namespace control_test
{
    class debuger
    {
        static public void trace(string msg)
        {
            System.Diagnostics.Debug.Write(msg + "\n");
        }
        
        static public void assert(bool condition)
        {
            System.Diagnostics.Debug.Assert(condition);
        }
        
    }
}
