package org.cellocad.MIT.dnacompiler;

import org.cellocad.BU.dom.DWire;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * {@code Wire} is a class that represents a 'wire' in a circuit. A wire connects two 
 * {@code Gate} objects in a circuit. In other words, a Wire is equivalent to an edge
 * in a graph (and a Gate serves as a node) and therefore has a Gate it leaves
 * {@code From} and a node it goes {@code To}. Several different constructors are provided.
 * <p>
 * Consistent with other classes, a {@code Wire} object here runs in 'reverse' to the 
 * direction viewed. An 'input' gate would have no outgoing wire. 
 * For example, the final output wire in a LogicCircuit is specified 
 * as going {@code From = output_gate} and {@code To = adjacent_gate}.
 *
 *
 *One simple way to construct a {@code Wire} is by calling:<p>
 *{@code Wire(int Index, Gate From, Gate To)} <p>
 *Other constructors are available to set other potentially useful properties. 
 *
 *@param Index integer index of given wire
 *@param From gate of origin for the wire defined as gate closer to outputs (gate wire leaves 'From')
 *@param To gate the wire connects to defined as gate closer to inputs (gate wire goes 'To')
 *@param Next sibling wire in NOR or OUTPUT_OR, points to child2 gate (i actually have no idea when this is used...)
 *
 *@author prashantvaidyanathan
 */

@JsonIgnoreProperties({"From", "To", "Next", "wire"})
public class Wire {

    public int Index;
    public String Name;


    public Gate From; //toward outputs
    public Gate To;   //toward inputs
    public Wire Next; //sibling wire in NOR or OUTPUT_OR


    public int From_index;
    public int To_index;
    public int Next_index;

    public DWire wire;

    public Wire()
    {
        this.Index = -1;
        this.Name = null;
        this.wire = null;
        this.From = null;
        this.To = null;
        this.Next = null;
        this.From_index = -1;
        this.To_index = -1;
        this.Next_index = -1;
    }
    public Wire(Wire w) {
        this.Index = w.Index;
        this.Name = w.Name;
        this.wire = w.wire;
        this.From = w.From;
        this.To = w.To;
        this.Next = w.Next;
        this.From_index = w.From_index;
        this.To_index = w.To_index;
        this.Next_index = w.Next_index;
    }
    public Wire(int indx,Gate dFrom,Gate dTo)
    {
        this.Index = indx;
        this.Name = null;
        this.wire = null;
        this.From = dFrom;
        this.To = dTo;
        this.Next = null;
    }
    public Wire(int indx,Gate dFrom,Gate dTo,Wire next)
    {
        this.Index = indx;
        this.Name = null;
        this.wire = null;
        this.From = dFrom;
        this.To = dTo;
        this.Next = next;
    }
    @Override
    public String toString()
    {
        String x= "Index:"+Index + " From:"+From.Index + " To:"+To.Index;
        return x;
    }
}
