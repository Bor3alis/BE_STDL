package fr.n7.stl.block;

class Driver {

	public static void main(String[] args) throws Exception {
		Parser parser = new Parser("fonctions.txt");
		parser.parse();
	}
	
}