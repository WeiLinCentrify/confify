package confify.util;

import confify.models.*;
import confify.models.output.AdminInfo;
import confify.models.output.ConferenceOutput;
import confify.models.output.ConferenceWithSpeakerAndAttendee;
import confify.models.output.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dennis on 4/23/2015.
 */
@Service
public class DataTransfer {
    @Autowired private PasswordEncoder pwdEncoder;
    public void userDataTransfer(User input, User output) {
        if (input.getEmail() != null) output.setEmail(input.getEmail());
        if (input.getFirstName() != null) output.setFirstName(input.getFirstName());
        if (input.getLastName() != null) output.setLastName(input.getLastName());
        if (input.getBio() != null) output.setBio(input.getBio());
        if (input.getAvatarUrl() != null) output.setAvatarUrl(input.getAvatarUrl());
        if (input.getPassword() != null) output.setPassword(pwdEncoder.encode(input.getPassword()));
        if (input.getOrganization() != null) output.setOrganization(input.getOrganization());
        if (input.getProfession() != null) output.setProfession(input.getProfession());
    }

    public void adminInput2Admin(Admin input, Admin output) {
        if (input.getEmail() != null) output.setEmail(input.getEmail());
        if (input.getFirstName() != null) output.setFirstName(input.getFirstName());
        if (input.getLastName() != null) output.setLastName(input.getLastName());
        if (input.getBio() != null) output.setBio(input.getBio());
        if (input.getAvatarUrl() != null) output.setAvatarUrl(input.getAvatarUrl());
        if (input.getPassword() != null) output.setPassword(pwdEncoder.encode(input.getPassword()));
        //if (input.getOrganization() != null) output.setOrganization(input.getOrganization());
    }

    public void user2UserInfo(User user, UserInfo userInfo) {
        userInfo.setId(user.getId());
        userInfo.setFirstName(user.getFirstName());
        userInfo.setLastName(user.getLastName());
        userInfo.setOrganization(user.getOrganization());
        userInfo.setBio(user.getBio());
        userInfo.setAvatarUrl(user.getAvatarUrl());
        userInfo.setEmail(user.getEmail());
        userInfo.setQrUrl(user.getQrUrl());
        userInfo.setProfession(user.getProfession());
    }

    public void admin2AdminInfo(Admin admin, AdminInfo adminInfo) {
        adminInfo.setId(admin.getId());
        adminInfo.setFirstName(admin.getFirstName());
        adminInfo.setLastName(admin.getLastName());
        adminInfo.setOrganization(admin.getOrganization());
        adminInfo.setBio(admin.getBio());
        adminInfo.setAvatarUrl(admin.getAvatarUrl());
        adminInfo.setEmail(admin.getEmail());
        adminInfo.setQrUrl(admin.getQrUrl());
        adminInfo.setOrganization(admin.getOrganization());
    }

    public void conferenceDataTransfer(Conference input, Conference output) {
        if (input.getName() != null) output.setName(input.getName());
        if (input.getDescription() != null) output.setDescription(input.getDescription());
        if (input.getVenue() != null) output.setVenue(input.getVenue());
        if (input.getOrganization() != null) output.setOrganization(input.getOrganization());
        if (input.getStartTime() != null) output.setStartTime(input.getStartTime());
        if (input.getEndTime() != null) output.setEndTime(input.getEndTime());
    }

    public void attend2ConferenceOutput(Attend attend, ConferenceOutput conferenceOutput) {
        conferenceOutput.setStatus(attend.getStatus());
        Conference conference = attend.getConference();
        conferenceOutput.setDescription(conference.getDescription());
        conferenceOutput.setEndTime(conference.getEndTime());
        conferenceOutput.setStartTime(conference.getStartTime());
        conferenceOutput.setId(conference.getId());
        conferenceOutput.setName(conference.getName());
        conferenceOutput.setVenue(conference.getVenue());
        conferenceOutput.setOrganization(conference.getOrganization());
    }

    public void attendList2ConferenceOutputList(List<Attend> attendList, List<ConferenceOutput> conferenceOutputList) {
        for (Attend attend : attendList) {
            ConferenceOutput conferOutput = new ConferenceOutput();
            attend2ConferenceOutput(attend, conferOutput);
            conferenceOutputList.add(conferOutput);
        }
    }

    public void conference2ConferenceInfoWithSpeakerAndAttendee(Conference conference, ConferenceWithSpeakerAndAttendee conferSA) {
        conferSA.setId(conference.getId());
        conferSA.setName((conference.getName()));
        conferSA.setOrganization(conference.getOrganization());
        conferSA.setVenue(conference.getVenue());
        conferSA.setStartTime(conference.getStartTime());
        conferSA.setEndTime(conference.getEndTime());
        conferSA.setDescription(conference.getDescription());
        List<UserInfo> attendeeList = new ArrayList();
        attendList2UserInfoList(conference.getAttends(),attendeeList);
        List<UserInfo> speakerList = new ArrayList();
        giveSpeechList2UserInfoList(conference.getGiveSpeeches(), speakerList);
        conferSA.setAttendees(attendeeList);
        conferSA.setSpeakers(speakerList);
    }

    public void giveSpeechList2UserInfoList(List<GiveSpeech> giveSpeechList, List<UserInfo> userInfoList) {
        for (GiveSpeech giveSpeech : giveSpeechList) {
            UserInfo userInfo = new UserInfo();
            giveSpeech2UserInfo(giveSpeech, userInfo);
            userInfoList.add(userInfo);
        }
    }

    public void attendList2UserInfoList(List<Attend> attendList, List<UserInfo> userInfoList) {
        for (Attend attend : attendList) {
            UserInfo userInfo = new UserInfo();
            attend2UserInfo(attend, userInfo);
            userInfoList.add(userInfo);
        }
    }

    public void attend2UserInfo(Attend attend, UserInfo output) {
        User attendee = attend.getAttendee();
        output.setId(attendee.getId());
        output.setFirstName(attendee.getFirstName());
        output.setLastName(attendee.getLastName());
        output.setEmail(attendee.getEmail());
        output.setBio(attendee.getBio());
        output.setOrganization(attendee.getOrganization());
        output.setAvatarUrl(attendee.getAvatarUrl());
        output.setQrUrl(attendee.getQrUrl());
        output.setProfession(attendee.getProfession());
        output.setStatus(attend.getStatus());
    }

    public void giveSpeech2UserInfo(GiveSpeech giveSpeech, UserInfo output) {
        User speaker = giveSpeech.getSpeaker();
        output.setId(speaker.getId());
        output.setFirstName(speaker.getFirstName());
        output.setLastName(speaker.getLastName());
        output.setEmail(speaker.getEmail());
        output.setBio(speaker.getBio());
        output.setOrganization(speaker.getOrganization());
        output.setAvatarUrl(speaker.getAvatarUrl());
        output.setQrUrl(speaker.getQrUrl());
        output.setProfession(speaker.getProfession());
    }
}
