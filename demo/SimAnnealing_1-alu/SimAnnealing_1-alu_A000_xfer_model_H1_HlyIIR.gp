
set output "SimAnnealing_1-alu_A000_xfer_model_H1_HlyIIR.eps"
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
set xlabel '00010001'
set arrow from 0.10079638663519258,0.001 to 0.10079638663519258,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 0.8905516954118254,0.001 to 0.8905516954118254,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.07
ymax = 2.5
K = 0.19
n = 2.6
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#8FC73E' title 'H1HlyIIR',\
 "<echo '1 2'" using (0.194258377):(1.2500000037910632)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.735147282):(0.1400000000187563)  with points pt 7 ps 4 lc rgb 'black' notitle
