package simulator.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class PhysicsSimulator {
	
	private double _time;  // numero de pasos que se ejecuta la simulacion
	private ForceLaws _forceLaws;  // leyes de la fuerza a aplicar
	private List<Body> _bodies = new ArrayList<>();  // cuerpos de la simulacion
	private double _dt;  // tiempo actual
	
	public PhysicsSimulator(ForceLaws FL, double tRealporPaso)
	{
		if(FL.equals(null))
		{
			this._forceLaws = FL;
		}
		else
		{
			throw new IllegalArgumentException("Ley de fuerza incorrecta");
		}
		
		if(tRealporPaso >= 0)
		{
			this._time = tRealporPaso;
		}
		else
		{
			throw new IllegalArgumentException("Tiempo no valido");
		}
		this._dt = 0.0;
	}
	
	public void advance(){
		for(Body b: this._bodies)
		{
			b.resetForce();
		}
		
		this._forceLaws.apply(_bodies);
		
		for(Body b: this._bodies)
		{
			b.move(_time);
			_dt += _time;
		}
	}
	
	public void addBody(Body b){
		if(!_bodies.contains(b))
		{
			_bodies.add(b);
		}
		else
		{
			throw new IllegalArgumentException("Ya existe el cuerpo");
		}
	}
	
	public JSONObject getState() {
		JSONObject j = new JSONObject();
		JSONArray ja = new JSONArray();
		
		/*String cadena = new String();
		cadena = "[ ";
		for(int i = 0; i < _bodies.size(); i ++)
		{
			/*if(i != _bodies.size() - 1)
			{
				cadena = cadena + _bodies.get(i).getState() + ", ";
			}
			else
			{
				cadena = cadena + _bodies.get(i).getState();
			}
		}
		cadena += " ] ";
		j.append("\"time\":", this._dt).append("\"bodies\":", cadena);*/
		
		for(Body b:this._bodies) {
			ja.put(b.getState());
		}
		
		j.put("bodies", ja);
		j.put("time", this._time);
		
		return j;
	}
	
	public String toString(){
		return getState().toString();
	}
}
