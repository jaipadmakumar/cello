
set output "SimAnnealing_1-alu_A000_xfer_model_I1_IcaRA.eps"
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
set xlabel '11001100'
set arrow from 0.0013,0.001 to 0.0013,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 4.4,0.001 to 4.4,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.08
ymax = 2.2
K = 0.1
n = 1.4
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#E83C96' title 'I1IcaRA',\
 "<echo '1 2'" using (0.105541491):(1.099999997660489)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (1.010814719):(0.16000000004233117)  with points pt 7 ps 4 lc rgb 'black' notitle
