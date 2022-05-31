package nextstep.subway.section.domain;

import nextstep.subway.station.domain.Station;
import nextstep.subway.station.dto.StationResponse;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Section> sections;

    protected Sections() {
        this.sections = new ArrayList<>();
    }

    private Sections(List<Section> sections) {
        this.sections = sections;
    }

    public static Sections empty() {
        return new Sections();
    }

    public static Sections from(List<Section> sections) {
        return new Sections(sections);
    }

    public void add(Section section) {
        if (!this.sections.isEmpty()) {
            relocateConnectSection(section);
        }
        this.sections.add(section);
    }

    public boolean containsStation(Station station) {
        return allStations().contains(station);
    }

    public int size() {
        return this.sections.size();
    }

    public List<Section> get() {
        return this.sections;
    }

    public List<StationResponse> allStationResponses() {
        return allStations().stream()
                .map(Station::toStationResponse)
                .collect(Collectors.toList());
    }

    private List<Station> allStations() {
        List<Station> allStations = new ArrayList<>();
        this.sections.forEach(section -> {
            allStations.addAll(section.getStations());
        });
        return allStations.stream().distinct().collect(Collectors.toList());
    }

    private void relocateConnectSection(Section addedSection) {
        Section connectSection = findConnectSection(addedSection);
        relocateUpSection(connectSection, addedSection);
        relocateDownSection(connectSection, addedSection);
    }

    private void relocateUpSection(Section connectSection, Section addedSection) {
        if (connectSection.isEqualsDownStation(addedSection.getDownStation())) {
            connectSection.updateDownStation(addedSection.getUpStation());
            connectSection.decreaseDistance(addedSection.getDistance());
        }
    }

    private void relocateDownSection(Section connectSection, Section addedSection) {
        if (connectSection.isEqualsUpStation(addedSection.getUpStation())) {
            connectSection.updateUpStation(addedSection.getDownStation());
            connectSection.decreaseDistance(addedSection.getDistance());
        }
    }

    private Section findConnectSection(Section section) {
        return findSectionByUpStation(section.getUpStation())
                .orElseGet(() -> findSectionByDownStation(section.getDownStation())
                        .orElseThrow(IllegalArgumentException::new)
                );
    }

    private Optional<Section> findSectionByUpStation(Station upStation) {
        return this.sections.stream()
                .filter(section -> section.isEqualsUpStation(upStation))
                .findFirst();
    }

    private Optional<Section> findSectionByDownStation(Station downStation) {
        return this.sections.stream()
                .filter(section -> section.isEqualsDownStation(downStation))
                .findFirst();
    }
}
