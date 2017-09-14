package org.dataarc.core.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.mongodb.core.geo.GeoJson;
import org.springframework.data.mongodb.core.geo.GeoJsonLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiLineString;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonMultiPolygon;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.geo.GeoJsonPolygon;

import com.mongodb.BasicDBObject;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPoint;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

/**
 * Converter for writing Mongo DB GeoJSON shapes from JTS {@link Geometry}
 * 
 * BSD License from : https://github.com/lcanet/spring-data-mongo-spatial/blob/master/src/main/java/org/tekila/datamongo/spatial/GeometryWriteConverter.java
 * 
 * @author Laurent Canet
 */
@WritingConverter
public enum GeometryWriteConverter implements Converter<Geometry, GeoJson> {
    INSTANCE;

    public GeoJson convert(Geometry g) {

        if (Polygon.class.isAssignableFrom(g.getClass())) {
            GeoJsonPolygon poly = buildCoordinates((Polygon) g);
            return poly;
        } else if (Point.class.isAssignableFrom(g.getClass())) {
            Point pt = (Point) g;
            return buildCoordinates(pt);
        } else if (LineString.class.isAssignableFrom(g.getClass())) {
            LineString ls = (LineString) g;
            return buildCoordinates(ls);
        } else if (MultiPoint.class.isAssignableFrom(g.getClass())) {
            MultiPoint mp = (MultiPoint) g;
            return buildCoordinates(mp);
        } else if (MultiLineString.class.isAssignableFrom(g.getClass())) {
            MultiLineString mls = (MultiLineString) g;
            return buildCoordinates(mls);
        } else if (MultiPolygon.class.isAssignableFrom(g.getClass())) {
            MultiPolygon mp = (MultiPolygon) g;
            return buildCoordinates(mp);

        } else {
            throw new IllegalArgumentException("Unsupported geometry type " + g.getClass().getName());
        }

    }

    private GeoJsonMultiPolygon buildCoordinates(MultiPolygon mls) {
        List<GeoJsonPolygon> points = new ArrayList<>();
        for (int i = 0; i < mls.getNumGeometries(); i++) {
            points.add(buildCoordinates((Polygon) mls.getGeometryN(i)));
        }
        GeoJsonMultiPolygon multi = new GeoJsonMultiPolygon(points);
        return multi;
    }

    private GeoJsonPolygon buildCoordinates(Polygon p) {

        List<org.springframework.data.geo.Point> points = new ArrayList<>();
        LineString exteriorRing = p.getExteriorRing();
        for (int i = 0; i < exteriorRing.getNumPoints(); i++) {
            points.add(buildCoordinates(exteriorRing.getPointN(i)));
        }
        GeoJsonPolygon polygon = new GeoJsonPolygon(points);
        for (int i = 0; i < p.getNumInteriorRing(); i++) {
            List<org.springframework.data.geo.Point> icoords = new ArrayList<>();
            LineString interiorRingN = p.getInteriorRingN(i);
            for (int j = 0; j < interiorRingN.getNumPoints(); j++) {
                icoords.add(buildCoordinates(interiorRingN.getPointN(j)));
            }
            polygon.withInnerRing(icoords);
        }

        return polygon;
    }

    private GeoJsonLineString buildCoordinates(LineString line) {
        List<org.springframework.data.geo.Point> points = new ArrayList<>();
        for (int i = 0; i < line.getNumPoints(); i++) {
            points.add(buildCoordinates(line.getPointN(i)));
        }
        GeoJsonLineString lineString = new GeoJsonLineString(points);
        return lineString;
    }

    private GeoJsonPoint buildCoordinates(Point pt) {
        return new GeoJsonPoint(pt.getX(), pt.getY());
    }

    private GeoJsonPoint buildCoordinates(Coordinate c) {
        return new GeoJsonPoint(c.x, c.y);
    }

    private GeoJsonMultiPoint buildCoordinates(MultiPoint mp) {
        List<org.springframework.data.geo.Point> points = new ArrayList<>();
        for (int i = 0; i < mp.getNumPoints(); i++) {
            points.add(buildCoordinates(mp.getCoordinates()[i]));
        }
        return new GeoJsonMultiPoint(points);
    }

    private GeoJsonMultiLineString buildCoordinates(MultiLineString mls) {
        List<GeoJsonLineString> lines = new ArrayList<>();
        for (int i = 0; i < mls.getNumGeometries(); i++) {
            lines.add(buildCoordinates((LineString) mls.getGeometryN(i)));
        }
        GeoJsonMultiLineString multi = new GeoJsonMultiLineString(lines);
        return multi;
    }

}