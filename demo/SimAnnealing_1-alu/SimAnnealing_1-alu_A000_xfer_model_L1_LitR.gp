
set output "SimAnnealing_1-alu_A000_xfer_model_L1_LitR.eps"
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
set xlabel '11110000'
set arrow from 0.0082,0.001 to 0.0082,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 2.5,0.001 to 2.5,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.07
ymax = 4.3
K = 0.05
n = 1.7
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#9C53A0' title 'L1LitR',\
 "<echo '1 2'" using (0.050983067):(2.1500000095977634)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.552709139):(0.14000000005610794)  with points pt 7 ps 4 lc rgb 'black' notitle
