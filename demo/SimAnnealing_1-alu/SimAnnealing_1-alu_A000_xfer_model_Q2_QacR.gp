
set output "SimAnnealing_1-alu_A000_xfer_model_Q2_QacR.eps"
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
set xlabel '01100110'
set arrow from 0.1779504533833275,0.001 to 0.1779504533833275,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 2.1701668586908798,0.001 to 2.1701668586908798,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.03
ymax = 2.8
K = 0.21
n = 2.4
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#66BC46' title 'Q2QacR',\
 "<echo '1 2'" using (0.21190396):(1.400000002021177)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (1.377679427):(0.06000000000148434)  with points pt 7 ps 4 lc rgb 'black' notitle
