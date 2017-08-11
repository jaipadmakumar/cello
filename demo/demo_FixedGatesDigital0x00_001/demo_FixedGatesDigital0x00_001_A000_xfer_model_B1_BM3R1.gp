
set output "demo_FixedGatesDigital0x00_001_A000_xfer_model_B1_BM3R1.eps"
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
set xlabel '10101010'
set arrow from 0.0034,0.001 to 0.0034,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 2.8,0.001 to 2.8,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.004
ymax = 0.5
K = 0.04
n = 3.4
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#EE2F2B' title 'B1BM3R1',\
 "<echo '1 2'" using (0.040190208):(0.25000000257630556)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.164716826):(0.00800000000618021)  with points pt 7 ps 4 lc rgb 'black' notitle
