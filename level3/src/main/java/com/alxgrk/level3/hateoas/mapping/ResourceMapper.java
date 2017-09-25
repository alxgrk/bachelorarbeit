package com.alxgrk.level3.hateoas.mapping;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

import com.alxgrk.level3.error.exceptions.timeslot.NoAvailableTimeslotsException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsNotAvailableException;
import com.alxgrk.level3.error.exceptions.timeslot.TimeslotsToBookClashException;
import com.alxgrk.level3.hateoas.rto.ResourceRto;
import com.alxgrk.level3.hateoas.rto.TimeslotRto;
import com.alxgrk.level3.models.Resource;
import com.alxgrk.level3.models.Timeslot;
import com.google.common.collect.Lists;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    ResourceMapper INSTANCE = Mappers.getMapper(ResourceMapper.class);

    ResourceRto resourceToResourceRto(Resource resource);

    Resource resourceRtoToResource(ResourceRto resource) throws NoAvailableTimeslotsException,
            TimeslotsNotAvailableException,
            TimeslotsToBookClashException;

    default void updateResourceFromResourceRto(ResourceRto rto,
            @MappingTarget Resource resource) throws NoAvailableTimeslotsException,
            TimeslotsNotAvailableException,
            TimeslotsToBookClashException {

        if (rto == null) {
            return;
        }

        resource.setName(rto.getName());

        List<Timeslot> list = timeslotRtoListToTimeslotList(rto.getAvailableTimeslots());
        if (list != null) {
            resource.setAvailableTimeslots(list);
        } else {
            resource.setAvailableTimeslots(Lists.newArrayList());
        }

        List<Timeslot> list1 = timeslotRtoListToTimeslotList(rto.getBookedTimeslots());
        if (list1 != null) {
            resource.setBookedTimeslots(list1);
        } else {
            resource.setAvailableTimeslots(Lists.newArrayList());
        }
    }

    // TIMESLOTS

    TimeslotRto timeslotToTimeslotRto(Timeslot timeslot);

    List<Timeslot> timeslotRtoListToTimeslotList(List<TimeslotRto> timeslots);

    default Timeslot timeslotRtoToTimeslot(TimeslotRto timeslot) {
        return new Timeslot(timeslot.getBeginning(), timeslot.getEnding());
    }
}