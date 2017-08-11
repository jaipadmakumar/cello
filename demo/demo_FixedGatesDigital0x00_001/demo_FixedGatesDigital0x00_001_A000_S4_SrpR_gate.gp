set output "demo_FixedGatesDigital0x00_001_A000_S4_SrpR_gate.eps"
set terminal postscript eps enhanced color "Helvetica, 35" size 6,6
set boxwidth 0.5
set lmargin 6
set border linewidth 2
set tics scale 2
set mytics 10
set key off
set xrange [0.5:8+0.5]
set yrange [0.001:100.0]
set logscale y
set style fill solid
plot "< awk '{if($4 == \"red\") print}'  demo_FixedGatesDigital0x00_001_A000_S4_SrpR_gate.dat" u 1:3:xtic(2) with boxes ls 1 lc rgb "dark-gray", \
     "< awk '{if($4 == \"blue\") print}' demo_FixedGatesDigital0x00_001_A000_S4_SrpR_gate.dat" u 1:3:xtic(2) with boxes ls 1 lc rgb "black" 
