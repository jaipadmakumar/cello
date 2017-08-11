module A(output out1,  input in1, in2, in3);
  always@(in1,in2,in3)
    begin
      case({in1,in2,in3})
        3'b001: {out1} = 1'b1;
        3'b010: {out1} = 1'b1;
        3'b100: {out1} = 1'b1;
        3'b111: {out1} = 1'b1;
      endcase
    end
endmodule