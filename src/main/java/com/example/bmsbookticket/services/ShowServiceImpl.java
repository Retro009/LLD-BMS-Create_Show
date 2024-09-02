package com.example.bmsbookticket.services;

import com.example.bmsbookticket.exceptions.*;
import com.example.bmsbookticket.models.*;
import com.example.bmsbookticket.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowServiceImpl implements ShowService{
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private ScreenRepository screenRepository;
    @Autowired
    private ShowRepository showRepository;
    @Autowired
    private SeatTypeShowRepository seatTypeShowRepository;
    @Autowired
    private SeatsRepository seatsRepository;
    @Autowired
    private ShowSeatRepository showSeatRepository;
    @Override
    public Show createShow(int userId, int movieId, int screenId, Date startTime, Date endTime, List<Pair<SeatType, Double>> pricingConfig, List<Feature> features) throws MovieNotFoundException, ScreenNotFoundException, FeatureNotSupportedByScreen, InvalidDateException, UserNotFoundException, UnAuthorizedAccessException {
        User user = userRepository.findById(userId).orElseThrow(()-> new UserNotFoundException("User Not Found"));
        if(!user.getUserType().equals(UserType.ADMIN))
            throw new UnAuthorizedAccessException("ACCESS DENIED");
        Movie movie = movieRepository.findById(movieId).orElseThrow(()-> new MovieNotFoundException("Movie Not Found"));
        Screen screen = screenRepository.findById(screenId).orElseThrow(()-> new ScreenNotFoundException("Screen Not Found"));
        List<Feature> featureList = screen.getFeatures();
        if(featureList.isEmpty() || featureList==null)
            throw new FeatureNotSupportedByScreen("Screen Doesnt support Features");
        if(!featureList.containsAll(features))
            throw new FeatureNotSupportedByScreen("Screen Doesnt support Features");
        if(endTime.before(startTime))
            throw new InvalidDateException("Invalid date Exception");
        Show show = new Show();
        show.setMovie(movie);
        show.setFeatures(features);
        show.setScreen(screen);
        show.setStartTime(startTime);
        show.setEndTime(endTime);


        for (Pair<SeatType, Double> pair : pricingConfig) {
            SeatTypeShow seatTypeShow = new SeatTypeShow();
            seatTypeShow.setShow(show);
            seatTypeShow.setSeatType(pair.getFirst());
            seatTypeShow.setPrice(pair.getSecond());
            seatTypeShowRepository.save(seatTypeShow);
        }


        List<Seat> seats = seatsRepository.findAll().stream()
                .filter(seat -> seat.getScreen().getId() == screenId)
                .collect(Collectors.toList());

        for (Seat seat : seats) {
            ShowSeat showSeat = new ShowSeat();
            showSeat.setShow(show);
            showSeat.setSeat(seat);
            showSeat.setStatus(SeatStatus.AVAILABLE);
            showSeatRepository.save(showSeat);
        }


        return showRepository.save(show);
    }
}
