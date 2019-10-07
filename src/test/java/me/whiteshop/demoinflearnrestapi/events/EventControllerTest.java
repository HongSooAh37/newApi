package me.whiteshop.demoinflearnrestapi.events;


import me.whiteshop.demoinflearnrestapi.common.BaseControllerTest;
import me.whiteshop.demoinflearnrestapi.common.TestDescription;
import org.hamcrest.Matchers;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class EventControllerTest extends BaseControllerTest {

    @Autowired
    EventRepository eventRepository;

    @Test
    @TestDescription("정상적으로 이벤트를 생성되는 테스트")
    public void createEvent() throws Exception {
        EventDto event = EventDto.builder()
                .name("Spring")
                .description("Spring REST API TEST")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,13,11))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,01,03))
                .endEventDateTime(LocalDateTime.of(2018,11,26,15,59))
                .basePrice(100)
                .maxPrice(500)
                .limitOfEnrollment(100)
                .location("선릉역 어딘가에 있는 회사에서 고생하는 룸메")
                .build()
                ;

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("id").exists())
                .andExpect(header().exists(HttpHeaders.LOCATION))
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE,MediaTypes.HAL_JSON_UTF8_VALUE))
                .andExpect(jsonPath("id").value(Matchers.not(100)))
                .andExpect(jsonPath("free").value(false))
                .andExpect(jsonPath("offline").value(true))
                .andExpect(jsonPath("eventStatus").value(EventStatus.DRAFT.name()))
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.query-events").exists())
                .andExpect(jsonPath("_links.update-event").exists())
                .andDo(document("create-event",
                        links(   linkWithRel("self").description("link to self"),
                                 linkWithRel("query-events").description("link to query events"),
                                 linkWithRel("update-event").description("link to update events"),
                                 linkWithRel("profile").description("link to update an existing event")
                        ),
                        requestHeaders(
                                headerWithName(HttpHeaders.ACCEPT).description("accept headers"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        requestFields(
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description Of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time Of begin Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time Of close Event"),
                                fieldWithPath("beginEventDateTime").description("date time Of begin Event"),
                                fieldWithPath("endEventDateTime").description("date time Of close Event"),
                                fieldWithPath("location").description("location Of new Event"),
                                fieldWithPath("basePrice").description("basePrice Of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice Of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment Of new Event")
                                ),
                        responseHeaders(
                                headerWithName(HttpHeaders.LOCATION).description("location headers"),
                                headerWithName(HttpHeaders.CONTENT_TYPE).description("content type header")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("id").description("id of new Event"),
                                fieldWithPath("name").description("Name of new Event"),
                                fieldWithPath("description").description("description Of new Event"),
                                fieldWithPath("beginEnrollmentDateTime").description("date time Of begin Event"),
                                fieldWithPath("closeEnrollmentDateTime").description("date time Of close Event"),
                                fieldWithPath("beginEventDateTime").description("date time Of begin Event"),
                                fieldWithPath("endEventDateTime").description("date time Of close Event"),
                                fieldWithPath("location").description("location Of new Event"),
                                fieldWithPath("basePrice").description("basePrice Of new Event"),
                                fieldWithPath("maxPrice").description("maxPrice Of new Event"),
                                fieldWithPath("limitOfEnrollment").description("limitOfEnrollment Of new Event"),
                                fieldWithPath("free").description("free or not"),
                                fieldWithPath("offline").description("offline or not"),
                                fieldWithPath("eventStatus").description("eventStatus"),
                                fieldWithPath("_links.self.href").description("link to self"),
                                fieldWithPath("_links.query-events.href").description("link to query event list"),
                                fieldWithPath("_links.update-event.href").description("link to update existing event"),
                                fieldWithPath("_links.profile.href").description("link to profile")
                        )
                ))
        ;
    }

    @Test
    @TestDescription("입력 값을 받을 수 없는 경우에 에러가 발생되는 테스트")
    public void createEvent_Bad_Request() throws Exception {
        Event event = Event.builder()
                .id(100)
                .name("Spring")
                .description("Spring REST API TEST")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,13,11))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,01,03))
                .endEventDateTime(LocalDateTime.of(2018,11,26,15,59))
                .basePrice(100)
                .maxPrice(500)
                .limitOfEnrollment(100)
                .location("선릉역 어딘가에 있는 회사에서 고생하는 룸메")
                .free(true)
                .offline(false)
                .eventStatus(EventStatus.PUBLISHED)
                .build()
                ;

        mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .accept(MediaTypes.HAL_JSON)
                .content(objectMapper.writeValueAsString(event)))
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 없는 경우에 에러가 발생되는 테스트")
    public void createEvent_Bad_Request_Empty_Input() throws Exception {
        EventDto eventDto = EventDto.builder().build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
            .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 에러가 발생되는 테스트")
    public void createEvent_Bad_Request_Wrong_Input() throws Exception {
        EventDto eventDto = EventDto.builder()
                .name("Spring")
                .description("Spring REST API TEST")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,26,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,25,13,11))
                .beginEventDateTime(LocalDateTime.of(2018,11,24,01,03))
                .endEventDateTime(LocalDateTime.of(2018,11,23,15,59))
                .basePrice(10000)
                .maxPrice(500)
                .limitOfEnrollment(100)
                .location("선릉역 어딘가에 있는 회사에서 고생하는 룸메")
                .build();

        this.mockMvc.perform(post("/api/events")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("content[0].objectName").exists())
                .andExpect(jsonPath("content[0].defaultMessage").exists())
                .andExpect(jsonPath("content[0].code").exists())
                .andExpect(jsonPath("_links.index").exists())
        ;
    }

    @Test
    @TestDescription("30개의 이벤트를 10개씩 두번째 페이지로 조회하기")
    public void queryEvent() throws Exception{
        //given
        IntStream.range(0,30).forEach(this::generateEvent);

        //when & then
        this.mockMvc.perform(get("/api/events")
                .param("page","1")
                .param("size","10")
                .param("sort","name,DESC"))
                .andDo(print()
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("page" ).exists())
                .andExpect(jsonPath("_embedded.eventList[0]._links.self").exists())
                .andExpect(jsonPath("_links.self").exists())
                .andExpect(jsonPath("_links.profile").exists())
                .andDo(document("qeury-events"))
        ;
    }

    @Test
    @TestDescription("기존의 이벤트를 하나 조회하기")
    public void getEvent() throws Exception{
        //given
       Event event =  this.generateEvent(100);

        //when & then
        this.mockMvc.perform(get("/api/events/{id}",event.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("name" ).exists())
                .andExpect(jsonPath("id" ).exists())
                .andExpect(jsonPath("_links.self" ).exists())
                .andExpect(jsonPath("_links.profile" ).exists())
                .andDo(document("get-an-event"))
        ;
    }

    @Test
    @TestDescription("없는 이벤트를 조회했을 때 404 응답 받기")
    public void getEvent404() throws Exception{
        //when & then
        this.mockMvc.perform(get("/api/event/12345"))
                .andExpect(status().isNotFound())
        ;

    }
    @Test
    @TestDescription("이벤트를 정상적으로 수정하기")
    public void updateEvent()  throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        String eventName = "Updated event";
        eventDto.setName(eventName);

        //when & then
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(this.objectMapper.writeValueAsString(eventDto))
                    )
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("name").value(eventName))
                    .andExpect(jsonPath("_links.self").exists())
        ;
    }

    @Test
    @TestDescription("입력 값이 비어있는 경우에 이벤트 수정 실패")
    public void updateEvent400_Empty()  throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = new EventDto();

        //when & then
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("입력 값이 잘못된 경우에 이벤트 수정 실패")
    public void updateEvent400_Wrong()  throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);
        eventDto.setBasePrice(20000);
        eventDto.setMaxPrice(1000);

        //when & then
        this.mockMvc.perform(put("/api/events/{id}",event.getId())
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isBadRequest())
        ;
    }

    @Test
    @TestDescription("존재하지 않는 이벤트 수정 실패")
    public void updateEvent404()  throws Exception{
        Event event = this.generateEvent(200);
        EventDto eventDto = this.modelMapper.map(event,EventDto.class);

        //when & then
        this.mockMvc.perform(put("/api/events/54321")
                .contentType(MediaType.APPLICATION_JSON_UTF8)
                .content(this.objectMapper.writeValueAsString(eventDto))
        )
                .andDo(print())
                .andExpect(status().isNotFound())
        ;
    }


    private Event generateEvent(int index) {
        Event event = Event.builder()
                .name("event" + index)
                .description("test event")
                .beginEnrollmentDateTime(LocalDateTime.of(2018,11,23,14,21))
                .closeEnrollmentDateTime(LocalDateTime.of(2018,11,24,13,11))
                .beginEventDateTime(LocalDateTime.of(2018,11,25,01,03))
                .endEventDateTime(LocalDateTime.of(2018,11,26,15,59))
                .basePrice(100)
                .maxPrice(500)
                .limitOfEnrollment(100)
                .location("선릉역 어딘가에 있는 회사에서 고생하는 룸메")
                .free(false)
                .offline(true)
                .eventStatus(EventStatus.DRAFT)
                .build();

        return this.eventRepository.save(event);
    }
}
