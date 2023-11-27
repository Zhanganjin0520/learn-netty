package com.netty.learn.time.server.orekit;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.hipparchus.ode.events.Action;
import org.hipparchus.util.FastMath;
import org.joda.time.DateTime;
import org.orekit.bodies.GeodeticPoint;
import org.orekit.bodies.OneAxisEllipsoid;
import org.orekit.data.ClasspathCrawler;
import org.orekit.data.DataContext;
import org.orekit.data.DataProvidersManager;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.frames.TopocentricFrame;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.tle.TLE;
import org.orekit.propagation.analytical.tle.TLEPropagator;
import org.orekit.propagation.events.ElevationExtremumDetector;
import org.orekit.propagation.events.EventDetector;
import org.orekit.propagation.events.handlers.EventHandler;
import org.orekit.propagation.events.handlers.RecordAndContinue;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScalesFactory;
import org.orekit.utils.Constants;
import org.orekit.utils.IERSConventions;

import java.util.Date;
import java.util.Iterator;


/**
 * @author Zhang Anjin
 * @description cal elevation
 * @date 2023/11/15 22:20
 */
@Slf4j
public class Elevation {

    static {
        // 初始化
        DataProvidersManager manager = DataContext.getDefault().getDataProvidersManager();
        manager.addProvider(new ClasspathCrawler("orekit-data.zip"));
    }

    public static final Frame ECEF = FramesFactory.getITRF(IERSConventions.IERS_2010, true);
    public static final OneAxisEllipsoid EARTH
            = new OneAxisEllipsoid(Constants.WGS84_EARTH_EQUATORIAL_RADIUS, Constants.WGS84_EARTH_FLATTENING, ECEF);

    public static void main(String[] args) {
        //TLE
        final String line1 = "1 52785U 22058A   23319.12631862  .00004094  00000+0  47950-3 0  9995";
        final String line2 = "2 52785  49.9960 313.9525 0007690 128.8250 231.3377 14.86833799 78911";
        final TLE tle = new TLE(line1, line2);
        final TLEPropagator propagator = TLEPropagator.selectExtrapolator(tle);
        //ground point
        final double lat = 36.0;
        final double lng = 120.0;
        GeodeticPoint point = new GeodeticPoint(FastMath.toRadians(lat), FastMath.toRadians(lng), 0);
        final TopocentricFrame tcf = new TopocentricFrame(EARTH, point, "point");
        //max elevation
        ElevationExtremumDetector detector = new ElevationExtremumDetector(tcf)
                .withThreshold(1.0e-6)
                .withHandler(new RecordAndContinue());
//                .withHandler(new Visibility());
        propagator.addEventDetector(detector);
        //calculate start and target time
        DateTime startTime = new DateTime();
        AbsoluteDate start = new AbsoluteDate(startTime.getYear(), startTime.getMonthOfYear(),
                startTime.getDayOfMonth(), startTime.getHourOfDay(), startTime.getMinuteOfHour(),
                startTime.getSecondOfMinute(), TimeScalesFactory.getUTC());
        DateTime targetTime = startTime.plusDays(1);
        AbsoluteDate target = new AbsoluteDate(targetTime.getYear(), targetTime.getMonthOfYear(),
                targetTime.getDayOfMonth(), targetTime.getHourOfDay(), targetTime.getMinuteOfHour(),
                targetTime.getSecondOfMinute(), TimeScalesFactory.getUTC());
        //calculate max elevation
        propagator.propagate(start, target);
        Iterator<EventDetector> iterator = propagator.getEventsDetectors().iterator();
        ElevationExtremumDetector eventDetector = (ElevationExtremumDetector) iterator.next();
        RecordAndContinue handler = (RecordAndContinue) eventDetector.getHandler();
        List<RecordAndContinue.Event> events = handler.getEvents();

        events.forEach(event -> {
            SpacecraftState state = event.getState();
            // Get the date of the extremum
            final Date date = state.getDate().toDate(TimeScalesFactory.getUTC());
            double elevation = FastMath.toDegrees(detector.getElevation(state));
//            if (elevation >= 0) {
                log.info("Maximum elevation at: " + date + " value (°): " + elevation);
//            }
        });


    }

    private static class Visibility implements EventHandler<ElevationExtremumDetector> {

        public Action eventOccurred(SpacecraftState s, ElevationExtremumDetector ed, boolean increasing) {
            // We have an elevation extremum

            // Get the corresponding elevation in degrees
            final double elevation = FastMath.toDegrees(ed.getElevation(s));

            // Get the date of the extremum
            final Date date = s.getDate().toDate(TimeScalesFactory.getUTC());

            // Print the data
            System.out.println("Maximum elevation at: " + date + " value (°): " + elevation);

            // Continue after event detection
            return Action.CONTINUE;
        }

    }
}
