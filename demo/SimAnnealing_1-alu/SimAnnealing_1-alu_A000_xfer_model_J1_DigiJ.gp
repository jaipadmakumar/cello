
set output "SimAnnealing_1-alu_A000_xfer_model_J1_DigiJ.eps"
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
set xlabel '01101001'
set arrow from 0.021788177379587152,0.001 to 0.021788177379587152,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 6.790374226377255,0.001 to 6.790374226377255,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.0
ymax = 100.0
K = 0.05
n = 3.0
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#004368' title 'J1DigiJ',\
 "<echo '1 2'" using (0.5):(0.0999000999000999)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.52):(0.0888206746107878)  with points pt 7 ps 4 lc rgb 'black' notitle
