
set output "SimAnnealing_0x00_001_A000_xfer_model_N1_LmrA.eps"
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
set xlabel '00111100'
set arrow from 0.09281329544717622,0.001 to 0.09281329544717622,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 2.168570861617254,0.001 to 2.168570861617254,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.2
ymax = 2.2
K = 0.18
n = 2.1
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#F04D23' title 'N1LmrA',\
 "<echo '1 2'" using (0.19804897):(1.100000001634402)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.512476199):(0.3999999998906917)  with points pt 7 ps 4 lc rgb 'black' notitle
