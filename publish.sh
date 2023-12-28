mvn release:clean
mvn release:prepare -Dresume=false
mvn release:perform -Darguments="-Dmaven.javadoc.skip=true" -X
