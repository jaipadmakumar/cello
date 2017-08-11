
set output "demo_FixedGatesDigital0x00_001_A000_xfer_model_F1_AmeR.eps"
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
set arrow from 0.042111734052802094,0.001 to 0.042111734052802094,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
set arrow from 0.8318670428294349,0.001 to 0.8318670428294349,1000.0 nohead lw 10 lt 2 lc rgb '#000000'
ymin = 0.2
ymax = 3.8
K = 0.09
n = 1.4
set dummy x

plot ymin+(ymax-ymin)/(1.0+(x/K)**n) lw 25 lc rgb '#6FCDE1' title 'F1AmeR',\
 "<echo '1 2'" using (0.097441924):(1.9000000041461786)  with points pt 7 ps 4 lc rgb 'black' notitle,\
 "<echo '1 2'" using (0.680980375):(0.39999999991903756)  with points pt 7 ps 4 lc rgb 'black' notitle
