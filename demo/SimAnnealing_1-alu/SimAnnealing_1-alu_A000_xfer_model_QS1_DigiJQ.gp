
set output "SimAnnealing_1-alu_A000_xfer_model_QS1_DigiJQ.eps"
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
set xlabel '00000110'
set arrow from 0.08323147996009803,0.001 to 0.08323147996009803,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 2.0245589801154136,0.001 to 2.0245589801154136,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.0
ymax = 100.0
K = 0.05
n = 3.0
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#004368' title 'QS1DigiJQ',\
 "<echo '1 2'" using (0.48):(0.11290045792425736)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.55):(0.07507507507507508)  with points pt 7 ps 4 lc rgb 'black' notitle
