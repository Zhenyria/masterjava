package ru.javaops.masterjava.service.xml;

import ru.javaops.masterjava.xml.schema.GroupType;
import ru.javaops.masterjava.xml.schema.PayloadType;
import ru.javaops.masterjava.xml.schema.ProjectType;
import ru.javaops.masterjava.xml.schema.UserType;
import ru.javaops.masterjava.xml.util.JaxbParser;
import ru.javaops.masterjava.xml.util.Schemas;

import javax.xml.bind.JAXBException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Handle XML and get users data
 */
public class MainXml {
    private final JaxbParser jaxbParser;
    private PayloadType payload;

    public MainXml(String pathToSchema, Class<?>... classesToBind) {
        this.jaxbParser = new JaxbParser(classesToBind);
        this.jaxbParser.setSchema(Schemas.ofClasspath(pathToSchema));
    }

    /**
     * Parse xml and load data
     *
     * @param pathToXml the xml file's way in classpath
     */
    public void loadData(String pathToXml) {
        try {
            this.payload = this.jaxbParser.unmarshal(this.getClass().getClassLoader().getResourceAsStream(pathToXml));
        } catch (JAXBException e) {
            throw new RuntimeException("The XML '" + pathToXml + "' parsing is impossible");
        }
    }

    /**
     * Get all users which bound with the project
     *
     * @param projectName project's name
     * @return sorted users list
     */
    public List<UserType> getUsersByProject(String projectName) {
        checkParsedPayloadExists();

        Set<GroupType> targetGroups = extractGroupsByProject(projectName);

        return targetGroups.isEmpty()
                ? Collections.emptyList()
                : extractUsersFromGroups(targetGroups);
    }

    /**
     * Get all groups which exist in the project
     *
     * @param projectName the name of a project
     * @return set of groups
     */
    private Set<GroupType> extractGroupsByProject(String projectName) {
        return Optional.ofNullable(payload)
                .map(PayloadType::getProjects)
                .map(PayloadType.Projects::getProject)
                .orElse(Collections.emptyList())
                .stream()
                .filter(project -> project.getName().equals(projectName))
                .findFirst()
                .map(ProjectType::getGroups)
                .map(ProjectType.Groups::getGroup)
                .map(Collection::stream)
                .orElse(Stream.empty())
                .collect(Collectors.toSet());
    }

    /**
     * Get all users which exist in the groups names of which in the set
     *
     * @param groups names of groups
     * @return users list
     */
    private List<UserType> extractUsersFromGroups(Set<GroupType> groups) {
        return Optional.ofNullable(payload)
                .map(PayloadType::getUsers)
                .map(PayloadType.Users::getUser)
                .orElse(Collections.emptyList())
                .stream()
                .filter(user -> user.getGroups() != null)
                .filter(user -> !user.getGroups().isEmpty())
                .filter(user -> hasCollectionGroup(user.getGroups(), groups))
                .collect(Collectors.toList());
    }

    /**
     * Get true if the one among groups exists in the prepared set else false
     *
     * @param groups       the collection of checked groups
     * @param targetGroups the prepared set of target groups
     */
    private boolean hasCollectionGroup(Collection<?> groups, Set<GroupType> targetGroups) {
        return Optional.ofNullable(groups)
                .orElse(Collections.emptyList())
                .stream()
                .filter(Objects::nonNull)
                .map(object -> (GroupType) object)
                .anyMatch(targetGroups::contains);
    }

    /**
     * Checks that xml was parsed and {@link MainXml#payload} is not null
     *
     * @throws NullPointerException if the payload was not loaded
     */
    private void checkParsedPayloadExists() {
        if (this.payload == null) {
            throw new NullPointerException("Parsed payload data does not exist");
        }
    }
}
