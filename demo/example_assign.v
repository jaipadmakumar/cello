module A(output out1, out2, input a, b);

// NOT: ~
// AND: &
// OR:  |
// order of operations: ()

//example: 
  wire w0;
  assign w0 = a & ~b;
  assign out1 = w0 | (a & c);


endmodule