/*
 * Copyright (c) 2002-2017 "Neo Technology,"
 * Network Engine for Objects in Lund AB [http://neotechnology.com]
 *
 * This product is licensed to you under the Apache License, Version 2.0 (the "License").
 * You may not use this product except in compliance with the License.
 *
 * This product may include a number of subcomponents with
 * separate copyright notices and license terms. Your use of the source
 * code for these subcomponents is subject to the terms and
 *  conditions of the subcomponent's license, as noted in the LICENSE file.
 */

package org.neo4j.ogm.typeconversion;

import org.junit.Test;

import org.neo4j.ogm.domain.convertible.bytes.PhotoWrapper;
import org.neo4j.ogm.metadata.ClassInfo;
import org.neo4j.ogm.metadata.MetaData;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Vince Bickers
 */
public class ByteArrayWrapperConversionTest {

    private static final MetaData metaData = new MetaData("org.neo4j.ogm.domain.convertible.bytes");
    private static final ClassInfo photoInfo = metaData.classInfo("PhotoWrapper");

    @Test
    public void testConvertersLoaded() {
        assertThat(photoInfo.propertyField("image").hasPropertyConverter()).isTrue();
    }

    @Test
    public void setImageAndCheck() {

        PhotoWrapper photo = new PhotoWrapper();
        AttributeConverter converter = photoInfo.propertyField("image").getPropertyConverter();

        photo.setImage(new Byte[]{1, 2, 3, 4});

        assertThat(converter.toGraphProperty(photo.getImage())).isEqualTo("AQIDBA==");
    }

    @Test
    public void getImageAndCheck() {

        PhotoWrapper photo = new PhotoWrapper();
        AttributeConverter converter = photoInfo.propertyField("image").getPropertyConverter();

        photo.setImage((Byte[]) converter.toEntityAttribute("AQIDBA=="));

        Byte[] image = photo.getImage();
        assertThat(image.length).isEqualTo(4);
        assertThat(image[0]).isEqualTo(Byte.decode("1"));
        assertThat(image[1]).isEqualTo(Byte.decode("2"));
        assertThat(image[2]).isEqualTo(Byte.decode("3"));
        assertThat(image[3]).isEqualTo(Byte.decode("4"));
    }
}
