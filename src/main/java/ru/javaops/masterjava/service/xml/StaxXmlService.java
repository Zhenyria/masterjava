package ru.javaops.masterjava.service.xml;

import ru.javaops.masterjava.model.UserData;
import ru.javaops.masterjava.xml.util.StaxStreamProcessor;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class StaxXmlService {
    private static final String userElementName = "User";
    private static final String userEmailAttributeName = "email";
    private static final String userNameElementName = "fullName";
    private static final String userGroupsElementName = "groups";

    private static final String projectElementName = "Project";
    private static final String projectNameElementName = "name";
    private static final String projectGroupsElementName = "groups";

    private static final String groupElementName = "Group";
    private static final String groupNameAttributeName = "name";

    private final String pathToXml;

    public StaxXmlService(String pathToXml) {
        this.pathToXml = pathToXml;
    }

    private StaxStreamProcessor getProcessor() {
        try {
            return new StaxStreamProcessor(this.getClass().getClassLoader().getResourceAsStream(pathToXml));
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }
    }

    public List<UserData> getUsersByProject(String projectName) {
        Set<User> users = new HashSet<>();
        Set<String> groupNames = new HashSet<>();

        String[] handledElementNames =
                Arrays.stream(HandledElementType.values())
                        .map(HandledElementType::getElementName)
                        .toArray(String[]::new);

        boolean isProjectWasFound = false;
        try (StaxStreamProcessor processor = getProcessor()) {
            while (!isProjectWasFound && processor.doUntil(XMLEvent.START_ELEMENT, handledElementNames)) {
                HandledElementType elementType = HandledElementType.getType(processor.getValue(XMLEvent.START_ELEMENT));
                if (elementType != null) {
                    switch (elementType) {
                        case PROJECT: {
                            if (checkProject(projectName, processor)) {
                                groupNames.addAll(parseProject(processor));
                                isProjectWasFound = true;
                            }
                            break;
                        }
                        case USER: {
                            User user = parseUser(processor);
                            if (user != null) {
                                users.add(user);
                            }
                            break;
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

        return users.stream()
                .filter(user -> !user.getGroups().isEmpty())
                .filter(user -> {
                    Set<String> usersGroups = user.getGroups();
                    return groupNames.size() >= usersGroups.size()
                            ? user.getGroups().stream().anyMatch(groupNames::contains)
                            : groupNames.stream().anyMatch(usersGroups::contains);
                })
                .map(user -> new UserData(user.getFullName(), user.getEmail()))
                .collect(Collectors.toList());
    }

    private boolean checkProject(String projectName, StaxStreamProcessor processor) throws XMLStreamException {
        return processor.getElementValue(projectNameElementName).equals(projectName);
    }

    private Set<String> parseProject(StaxStreamProcessor processor) throws XMLStreamException {
        Set<String> groupNames = new HashSet<>();
        processor.doUntil(XMLEvent.START_ELEMENT, projectGroupsElementName);
        while (processor.parseElementUntil(projectGroupsElementName, groupElementName)) {
            groupNames.add(processor.getAttributeValue(groupNameAttributeName));
        }
        return groupNames;
    }

    private User parseUser(StaxStreamProcessor processor) throws XMLStreamException {
        String email = processor.getAttributeValue(userEmailAttributeName);
        String name = processor.getElementValue(userNameElementName);
        String userGroups;
        if (!processor.parseElementUntil(userElementName, userGroupsElementName)
            || (userGroups = processor.getCurrentElementValue()) == null
            || userGroups.isEmpty()) {
            return null;
        }
        return new User(name, email, Arrays.stream(userGroups.split(" ")).collect(Collectors.toSet()));
    }

    /**
     * Type of main parsed element
     */
    private enum HandledElementType {
        USER(userElementName),
        PROJECT(projectElementName);

        private final String elementName;

        HandledElementType(String elementName) {
            this.elementName = elementName;
        }

        public static HandledElementType getType(String elementName) {
            return Arrays.stream(HandledElementType.values())
                    .filter(type -> type.elementName.equals(elementName))
                    .findFirst()
                    .orElse(null);
        }

        public String getElementName() {
            return elementName;
        }
    }

    /**
     * Data container for temporal saving users data
     */
    private static class User {
        private final String fullName;
        private final String email;
        private final Set<String> groups;

        public User(String name, String email, Set<String> groups) {
            this.fullName = name;
            this.email = email;
            this.groups = groups;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public Set<String> getGroups() {
            return groups;
        }
    }
}
