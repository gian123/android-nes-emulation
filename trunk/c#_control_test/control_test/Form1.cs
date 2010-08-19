using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;

namespace control_test
{
    public partial class Form1 : Form
    {
        private graphicView _gControl = new graphicView();

        public Form1()
        {
            InitializeComponent();
            _gControl.Dock = DockStyle.Fill;
            this.Controls.Add(_gControl);
        }
    }
}