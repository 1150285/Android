using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace Ex2
{
    public partial class Form1 : Form
    {
        public Form1()
        {
            InitializeComponent();
        }

        private void btnClica_Click(object sender, EventArgs e)
        {
            for (int i=0; i<5;i++)
            {
                MessageBox.Show("Fui ver a fonte","Já foste",MessageBoxButtons.OK,MessageBoxIcon.Warning);
				MessageBox.Show("Alteração user unknown :)","Já foste",MessageBoxButtons.OK,MessageBoxIcon.Warning);
				
            }
            
        }
    }
}
