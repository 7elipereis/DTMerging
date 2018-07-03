package Utils;

import java.io.File;
import java.io.IOException;

import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;

public class GraphVizHelper {
	Graphviz gv;
	public void print2PNG(String graph, String label, String filePath) {		
		graph = graph.replace("}", "label=\""+label+"\";labelloc=top;labeljust=center;}");
		gv = Graphviz.fromString(graph);
		try {
			gv.width(800).render(Format.PNG).toFile(new File(filePath));
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} finally {
			gv = null;
		}
	}

	
}
