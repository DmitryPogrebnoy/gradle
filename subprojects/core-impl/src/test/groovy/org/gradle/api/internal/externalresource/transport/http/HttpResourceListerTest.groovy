/*
 * Copyright 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.gradle.api.internal.externalresource.transport.http

import org.gradle.api.internal.externalresource.ExternalResource
import spock.lang.Specification

class HttpResourceListerTest extends Specification {

    HttpResourceAccessor accessorMock = Mock(HttpResourceAccessor)
    ExternalResource externalResource = Mock(ExternalResource)
    HttpResourceLister lister = new HttpResourceLister(accessorMock)

    def "addTrailingSlashes adds trailing slashes on relative URL if not exist"() {
        expect:
        new URL(resultingURL) == lister.addTrailingSlashes(new URL(inputURL))
        where:
        inputURL                        | resultingURL
        "http://testrepo"               | "http://testrepo/"
        "http://testrepo/"              | "http://testrepo/"
        "http://testrepo/index.html"    | "http://testrepo/index.html"
    }

    def "consumeExternalResource closes resource after reading into stream"() {
        setup:
        accessorMock.getResource("http://testrepo/") >> externalResource;
        when:
        lister.loadResourceContent(externalResource)
        then:
        1 * externalResource.writeTo(_, _)
        1 * externalResource.close()
    }

    def "list returns null if HttpAccessor returns null"(){
        setup:
        accessorMock.getResource("http://testrepo/")  >> null
        expect:
        null == lister.list("http://testrepo")
    }
}
