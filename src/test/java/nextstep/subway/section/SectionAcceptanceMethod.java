package nextstep.subway.section;

import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.section.dto.SectionRequest;
import nextstep.subway.section.dto.SectionResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

import static nextstep.subway.BaseAcceptanceTest.*;
import static nextstep.subway.line.LineAcceptanceMethod.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SectionAcceptanceMethod {
    private static final String SECTIONS_URI = "/sections";
    private static final String PARAM_NAME_STATION_ID = "stationId";

    public static ExtractableResponse<Response> 지하철_구간_추가(ExtractableResponse<Response> 지하철노선_생성_응답, SectionRequest 지하철구간_요청) {
        return post(지하철구간_요청_URI(지하철노선_생성_응답), 지하철구간_요청);
    }

    public static void 지하철구간_추가됨(ExtractableResponse<Response> 구간_추가_응답, SectionRequest 구간_추가_요청) {
        SectionResponse sectionResponse = 구간_추가_응답.jsonPath().getObject(".", SectionResponse.class);
        assertAll (
                () -> assertThat(sectionResponse.getUpStationId()).isEqualTo(구간_추가_요청.getUpStationId()),
                () -> assertThat(sectionResponse.getDownStationId()).isEqualTo(구간_추가_요청.getDownStationId()),
                () -> assertThat(sectionResponse.getDistance()).isEqualTo(구간_추가_요청.getDistance())
        );
    }

    public static void 추가된_지하철구간_조회(ExtractableResponse<Response> 지하철노선_생성_응답, SectionRequest 지하철구간_요청) {
        지하철구간에_포함된_역_조회(지하철노선_생성_응답, 지하철구간_요청.getUpStationId(), 지하철구간_요청.getDownStationId());
    }

    public static void 새로운_구간_추가_안됨(ExtractableResponse<Response> 구간_추가_응답) {
        내부_서버_에러_응답_확인(구간_추가_응답);
    }

    public static ExtractableResponse<Response> 지하철_구간_제거(ExtractableResponse<Response> 지하철노선_생성_응답, Long stationId) {
        UriComponents uri = UriComponentsBuilder.fromPath(지하철구간_요청_URI(지하철노선_생성_응답))
                .queryParam(PARAM_NAME_STATION_ID, stationId)
                .build();

        return delete(uri.toUriString());
    }

    public static void 새로운_종점_조회(ExtractableResponse<Response> 지하철노선_생성_응답, Long 조회할_역_ID) {
        지하철구간에_포함된_역_조회(지하철노선_생성_응답, 조회할_역_ID);
    }

    public static void 제거된역_조회_안됨(ExtractableResponse<Response> 지하철노선_생성_응답, Long 제거된_역_ID) {
        List<Long> stationIds = 지하철역_ID_목록_조회(지하철노선_생성_응답);
        assertThat(stationIds).doesNotContain(제거된_역_ID);
    }

    public static void 지하철_구간_제거_안됨(ExtractableResponse<Response> 구간_제거_응답) {
        내부_서버_에러_응답_확인(구간_제거_응답);
    }

    private static String 지하철구간_요청_URI(ExtractableResponse<Response> 지하철노선_생성_응답) {
        return String.format("%s%s", 지하철노선_생성_응답.header(HttpHeaders.LOCATION), SECTIONS_URI);
    }

    private static List<Long> 지하철역_ID_목록_조회(ExtractableResponse<Response> 지하철노선_생성_응답) {
        ExtractableResponse<Response> 지하철노선_조회_응답 = 지하철노선_ID_조회(지하철노선_생성_응답);
        return 지하철노선_조회_응답.jsonPath().getList("stations.id", Long.class);
    }

    private static void 지하철구간에_포함된_역_조회(ExtractableResponse<Response> 지하철노선_생성_응답, Long... 조회할_역_ID) {
        List<Long> stationIds = 지하철역_ID_목록_조회(지하철노선_생성_응답);
        assertThat(stationIds).contains(조회할_역_ID);
    }

    private static void 내부_서버_에러_응답_확인(ExtractableResponse<Response> 응답_객체) {
        assertThat(응답_객체.statusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR.value());
    }
}
