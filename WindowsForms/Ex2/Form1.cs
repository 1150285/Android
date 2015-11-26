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
            Random p = new Random();
            
            //Random de 1 a 7... Número espaços para balas
            int num = p.Next(1, 7);


            // jogada
            int jogada = p.Next(1, 7);
            if (num == jogada)
            {
                // Morreste
                MessageBox.Show("Pum! Morreste", "Já foste", MessageBoxButtons.OK, MessageBoxIcon.Stop);
            }
            else
            {
                // Tiveste sorte
                MessageBox.Show("Desta vez tiveste sorte, da próxima .... já não sei!", "Sorte", MessageBoxButtons.OK, MessageBoxIcon.Information);
            }
        }
    }
}
