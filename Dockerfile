FROM adoptopenjdk:11.0.8_10-jre-openj9-0.21.0

VOLUME /tmp

ARG DEPENDENCY=target/dependency

COPY ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY ${DEPENDENCY}/META-INF /app/META-INF
COPY ${DEPENDENCY}/BOOT-INF/classes /app

ENTRYPOINT ["java","-cp","app:app/lib/*","de.innovationhub.prox.professorprofileservice.ProfessorProfileService"]
