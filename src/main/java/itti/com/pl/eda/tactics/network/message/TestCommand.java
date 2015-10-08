package itti.com.pl.eda.tactics.network.message;

public enum TestCommand {

	AddClassesToTheOntology,
	ReadOntologySize, 
	ReadRepositorySize,
	PerformSelectOperation, 
	PerformInsertOperation,
	PerformFullInsertOperation, 
	//used to remove all TMP classes from the ontology
	RemoveDummyClasses, 
	RemoveDummyInstances,
	;

	public static TestCommand getTestCommand(String command) {

		if(command == null){
			return null;
		}else{
			for (TestCommand cmd : TestCommand.values()) {
				if(cmd.name().equals(command)){
					return cmd;
				}
			}
			return null;
		}
	}
}
