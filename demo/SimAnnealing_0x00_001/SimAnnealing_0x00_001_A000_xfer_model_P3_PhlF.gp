
set output "SimAnnealing_0x00_001_A000_xfer_model_P3_PhlF.eps"
set terminal postscript eps enhanced color "Helvetica, 35" size 2,2
set logscale x
set logscale y
set lmargin screen 0.0
set rmargin screen 1.0
set tmargin screen 1.0
set bmargin screen 0.0
set size ratio 1.0
set border linewidth 2
set tics scale 2
set mxtics 10
set mytics 10
set key bottom left
set key samplen -1
set xrange [0.001:1000.0]
set yrange [0.001:1000.0]
set format y "10^{%L}"    
set format x "10^{%L}"    
set format x ""    
set xlabel '00000011'
set arrow from 0.18017074484795562,0.001 to 0.18017074484795562,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 3.80629791191481,0.001 to 3.80629791191481,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.02
ymax = 6.8
K = 0.23
n = 4.2
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#F9A427' title 'P3PhlF',\
 "<echo '1 2'" using (0.230323307):(3.400000001301316)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.920133536):(0.04000000004283734)  with points pt 7 ps 4 lc rgb 'black' notitle
