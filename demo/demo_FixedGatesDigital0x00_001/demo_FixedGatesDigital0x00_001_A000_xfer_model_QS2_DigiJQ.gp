
set output "demo_FixedGatesDigital0x00_001_A000_xfer_model_QS2_DigiJQ.eps"
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
set xlabel '11101110'
set arrow from 0.010069148762069454,0.001 to 0.010069148762069454,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 2.367817295187728,0.001 to 2.367817295187728,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.0
ymax = 100.0
K = 0.05
n = 3.0
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#004368' title 'QS2DigiJQ',\
 "<echo '1 2'" using (0.51):(0.09414351991323734)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.56):(0.0711273977045766)  with points pt 7 ps 4 lc rgb 'black' notitle
