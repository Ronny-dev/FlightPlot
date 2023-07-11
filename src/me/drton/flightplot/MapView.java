package me.drton.flightplot;

import org.jfree.data.xy.XYSeries;
import org.jxmapviewer.JXMapKit;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.viewer.*;
import javax.swing.*;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.*;
import java.util.List;

public class MapView {

    private final XYSeries latSeries;
    private final XYSeries lonSeries;
    private ArrayList<LocationPoint> locationPointArrayList;
    private WaypointPainter<Waypoint> waypointPainter;

    public MapView(XYSeries latSeries, XYSeries lonSeries) {
        this.latSeries = latSeries;
        this.lonSeries = lonSeries;
    }

    public void setLocationTag(float x) {
        if (locationPointArrayList == null) return;
        if (waypointPainter == null) return;
        if (locationPointArrayList.isEmpty()) return;
        LocationPoint targetPoint = null;
        for (LocationPoint point : locationPointArrayList) {
            if (point.getTimestamp() - x > 0) {
                targetPoint = point;
                break;
            }
        }
        if (targetPoint != null) {
            final GeoPosition geoPosition = new GeoPosition(targetPoint.lat, targetPoint.lon);
            System.out.println(targetPoint.lat + "::" + targetPoint.lon);
            waypointPainter.setWaypoints(new HashSet<Waypoint>(Collections.singletonList(new DefaultWaypoint(geoPosition))));
        }
    }

    public void show() {
        setNetworkAgent();

        //图层
        JXMapKit jXMapKit = new JXMapKit();
        TileFactoryInfo info = new OSMTileFactoryInfo();
        DefaultTileFactory tileFactory = new DefaultTileFactory(info);
        jXMapKit.setTileFactory(tileFactory);

        locationPointArrayList = buildLocationList(latSeries, lonSeries);

        if (locationPointArrayList != null && !locationPointArrayList.isEmpty()) {

            List<GeoPosition> track = new ArrayList<GeoPosition>();

            for (LocationPoint point : locationPointArrayList) {
                track.add(new GeoPosition(point.lat, point.lon));
            }

            //线的图层
            RoutePainter routePainter = new RoutePainter(track);
            //点的图层
            waypointPainter = new WaypointPainter<Waypoint>();

            //画线
            List<Painter<JXMapViewer>> painters = new ArrayList<Painter<JXMapViewer>>();
            painters.add(routePainter);
            painters.add(waypointPainter);
            jXMapKit.getMainMap().zoomToBestFit(new HashSet<GeoPosition>(track), 0.7);
            CompoundPainter<JXMapViewer> painter = new CompoundPainter<JXMapViewer>(painters);
            jXMapKit.getMainMap().setOverlayPainter(painter);
        }

        // Display the viewer in a JFrame
        JFrame frame = new JFrame("JXMapviewer2 Example 6");
        frame.getContentPane().add(jXMapKit);
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    private ArrayList<LocationPoint> buildLocationList(XYSeries latSeries, XYSeries lonSeries) {

        if (latSeries == null || lonSeries == null) return null;

        ArrayList<LocationPoint> locationPointArrayList = new ArrayList<LocationPoint>();

        if (latSeries.getItemCount() != lonSeries.getItemCount()) return null;

        int count = latSeries.getItemCount();

        for (int i = 0; i < count; i++) {
            double lat = latSeries.getY(i).doubleValue();
            double lon = lonSeries.getY(i).doubleValue();
            float time = latSeries.getX(i).floatValue();
            if (Double.isNaN(lat) || Double.isNaN(lon)) continue;
            LocationPoint point = new LocationPoint();
            point.setLat(lat);
            point.setLon(lon);
            point.setTimestamp(time);
            locationPointArrayList.add(point);
        }

        return locationPointArrayList;
    }

    private void setNetworkAgent() {

        String proxyHost = "127.0.0.1";
        String proxyPort = "1080";

        System.setProperty("http.proxyHost", proxyHost);
        System.setProperty("http.proxyPort", proxyPort);
    }

    private class LocationPoint {
        private double lat;
        private double lon;
        private float timestamp;

        public float getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(float timestamp) {
            this.timestamp = timestamp;
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }
    }

    public static class RoutePainter implements Painter<JXMapViewer>
    {
        private Color color = Color.RED;
        private boolean antiAlias = true;

        private java.util.List<GeoPosition> track;

        /**
         * @param track the track
         */
        public RoutePainter(java.util.List<GeoPosition> track)
        {
            // copy the list so that changes in the
            // original list do not have an effect here
            this.track = new ArrayList<GeoPosition>(track);
        }

        @Override
        public void paint(Graphics2D g, JXMapViewer map, int w, int h)
        {
            g = (Graphics2D) g.create();

            // convert from viewport to world bitmap
            Rectangle rect = map.getViewportBounds();
            g.translate(-rect.x, -rect.y);

            if (antiAlias)
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // do the drawing
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(4));

            drawRoute(g, map);

            // do the drawing again
            g.setColor(color);
            g.setStroke(new BasicStroke(2));

            drawRoute(g, map);

            g.dispose();
        }

        /**
         * @param g the graphics object
         * @param map the map
         */
        private void drawRoute(Graphics2D g, JXMapViewer map)
        {
            int lastX = 0;
            int lastY = 0;

            boolean first = true;

            for (GeoPosition gp : track)
            {
                // convert geo-coordinate to world bitmap pixel
                Point2D pt = map.getTileFactory().geoToPixel(gp, map.getZoom());

                if (first)
                {
                    first = false;
                }
                else
                {
                    g.drawLine(lastX, lastY, (int) pt.getX(), (int) pt.getY());
                }

                lastX = (int) pt.getX();
                lastY = (int) pt.getY();
            }
        }
    }
}
