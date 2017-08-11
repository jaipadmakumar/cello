set output "demo_FixedGates0xFE_002_A000_E1_BetI_gate.eps"
set terminal postscript  eps  enhanced color "Helvetica, 35" size 8,8
reset
set xtics nomirror
set tics scale 2
set logscale x
set format y "" 
set format x "" 
set xtics (0.001, 0.01, 0.1, 0, 1, 10, 100)
set ytics 0.01
set border 15 lw 2
set lmargin 5
set bmargin 0
set rmargin 0
set tmargin 0
max=500 #max value
min=0.0005 #min value
set xrange [min:max]
set yrange [-0.0002:0.0569273]
set style fill solid 0.5 #fillstyle
set multiplot layout 10,1 rowsfirst
set ylabel "000" rotate by 0
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):2 w boxes lc rgb "black" notitle
set ylabel "001" rotate by 0
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):3 w boxes lc rgb "dark-gray" notitle
set ylabel "010" rotate by 0
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):4 w boxes lc rgb "black" notitle
set ylabel "011" rotate by 0
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):5 w boxes lc rgb "dark-gray" notitle
set ylabel "100" rotate by 0
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):6 w boxes lc rgb "black" notitle
set ylabel "101" rotate by 0
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):7 w boxes lc rgb "dark-gray" notitle
set ylabel "110" rotate by 0
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):8 w boxes lc rgb "black" notitle
set ylabel "111" rotate by 0
set format x "10^{%T}" 
set xlabel "Output (RPU)" 
plot "demo_FixedGates0xFE_002_A000_E1_BetI_gate.txt" u (10**$1):9 w boxes lc rgb "dark-gray" notitle
unset multiplot
