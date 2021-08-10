/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.hadoop.conf;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import javax.annotation.Nullable;

import org.apache.hadoop.classification.InterfaceAudience;
import org.apache.hadoop.classification.InterfaceStability;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.util.ReflectionUtils;
import org.apache.hadoop.util.StringUtils;

/**
 * Simple hijacked configuration used to remove dependencies.
 */
@InterfaceAudience.Public
@InterfaceStability.Stable
public class Configuration implements Iterable<Map.Entry<String, String>>, Writable
{
    static final String UNKNOWN_RESOURCE = "Unknown";

    private Set<String> finalParameters = Collections.newSetFromMap (new ConcurrentHashMap<String, Boolean> ());
    private static final WeakHashMap<Configuration, Object> REGISTRY = new WeakHashMap<Configuration, Object> ();

    private static final Map<ClassLoader, Map<String, WeakReference<Class<?>>>> CACHE_CLASSES = new WeakHashMap<ClassLoader, Map<String, WeakReference<Class<?>>>> ();

    /**
     * Sentinel value to store negative cache results in {@link #CACHE_CLASSES}.
     */
    private static final Class<?> NEGATIVE_CACHE_SENTINEL = NegativeCacheSentinel.class;

    /**
     * A pending addition to the global set of deprecated keys.
     */
    public static class DeprecationDelta
    {
        private final String key;
        private final String[] newKeys;
        private final String customMessage;

        DeprecationDelta (String key, String[] newKeys, String customMessage)
        {
            this.key = key;
            this.newKeys = newKeys;
            this.customMessage = customMessage;
        }

        public DeprecationDelta (String key, String newKey, String customMessage)
        {
            this (key, new String[]
            { newKey }, customMessage);
        }

        public DeprecationDelta (String key, String newKey)
        {
            this (key, new String[]
            { newKey }, null);
        }

        public String getKey ()
        {
            return key;
        }

        public String[] getNewKeys ()
        {
            return newKeys;
        }

        public String getCustomMessage ()
        {
            return customMessage;
        }
    }

    public static void addDeprecations (DeprecationDelta[] deltas)
    {
    }

    @Deprecated
    public static void addDeprecation (String key, String[] newKeys, String customMessage)
    {
    }

    public static void addDeprecation (String key, String newKey, String customMessage)
    {
    }

    @Deprecated
    public static void addDeprecation (String key, String[] newKeys)
    {
    }

    public static void addDeprecation (String key, String newKey)
    {
    }

    public static boolean isDeprecated (String key)
    {
        return false;
    }

    public void setDeprecatedProperties ()
    {
    }

    private Properties properties;
    private ClassLoader classLoader;
    {
        classLoader = Thread.currentThread ().getContextClassLoader ();
        if (classLoader == null)
        {
            classLoader = Configuration.class.getClassLoader ();
        }
    }

    public Configuration ()
    {
        this (true);
    }

    public Configuration (boolean loadDefaults)
    {
        synchronized (Configuration.class)
        {
            REGISTRY.put (this, null);
        }
    }

    public Configuration (Configuration other)
    {
    }

    public static synchronized void reloadExistingConfigurations ()
    {
    }

    public static synchronized void addDefaultResource (String name)
    {
    }

    public static void setRestrictSystemPropertiesDefault (boolean val)
    {
    }

    public void setRestrictSystemProperties (boolean val)
    {
    }

    public void addResource (String name)
    {
    }

    public void addResource (String name, boolean restrictedParser)
    {
    }

    public void addResource (URL url)
    {
    }

    public void addResource (URL url, boolean restrictedParser)
    {
    }

    public void addResource (Path file)
    {
    }

    public void addResource (Path file, boolean restrictedParser)
    {
    }

    public void addResource (InputStream in)
    {
    }

    public void addResource (InputStream in, boolean restrictedParser)
    {
    }

    public void addResource (InputStream in, String name)
    {
    }

    public void addResource (InputStream in, String name, boolean restrictedParser)
    {
    }

    public void addResource (Configuration conf)
    {
    }

    public synchronized void reloadConfiguration ()
    {
    }

    String getenv (String name)
    {
        return System.getenv (name);
    }

    String getProperty (String key)
    {
        return System.getProperty (key);
    }

    public String get (String name)
    {
        return null;
    }

    public void setAllowNullValueProperties (boolean val)
    {
    }

    public void setRestrictSystemProps (boolean val)
    {
    }

    public boolean onlyKeyExists (String name)
    {
        return false;
    }

    public String getTrimmed (String name)
    {
        String value = get (name);

        if (null == value)
        {
            return null;
        }
        else
        {
            return value.trim ();
        }
    }

    public String getTrimmed (String name, String defaultValue)
    {
        String ret = getTrimmed (name);
        return ret == null ? defaultValue : ret;
    }

    public String getRaw (String name)
    {
        return null;
    }

    public void set (String name, String value)
    {
    }

    public void set (String name, String value, String source)
    {
    }

    void logDeprecation (String message)
    {
    }

    void logDeprecationOnce (String name, String source)
    {
    }

    public synchronized void unset (String name)
    {
    }

    public synchronized void setIfUnset (String name, String value)
    {
    }

    public String get (String name, String defaultValue)
    {
        return defaultValue;
    }

    public int getInt (String name, int defaultValue)
    {
        return defaultValue;
    }

    public int[] getInts (String name)
    {
        String[] strings = getTrimmedStrings (name);
        int[] ints = new int[strings.length];
        for (int i = 0; i < strings.length; i++)
        {
            ints[i] = Integer.parseInt (strings[i]);
        }
        return ints;
    }

    public void setInt (String name, int value)
    {
    }

    public long getLong (String name, long defaultValue)
    {
        return defaultValue;
    }

    public long getLongBytes (String name, long defaultValue)
    {
        return defaultValue;
    }

    public void setLong (String name, long value)
    {
    }

    public float getFloat (String name, float defaultValue)
    {
        return defaultValue;
    }

    public void setFloat (String name, float value)
    {
    }

    public double getDouble (String name, double defaultValue)
    {
        return defaultValue;
    }

    public void setDouble (String name, double value)
    {
    }

    public boolean getBoolean (String name, boolean defaultValue)
    {
        return defaultValue;
    }

    public void setBoolean (String name, boolean value)
    {
    }

    public void setBooleanIfUnset (String name, boolean value)
    {
    }

    public <T extends Enum<T>> void setEnum (String name, T value)
    {
    }

    public <T extends Enum<T>> T getEnum (String name, T defaultValue)
    {
        return defaultValue;
    }

    enum ParsedTimeDuration
    {
        NS
        {
            @Override
            TimeUnit unit ()
            {
                return TimeUnit.NANOSECONDS;
            }

            @Override
            String suffix ()
            {
                return "ns";
            }
        },
        US
        {
            @Override
            TimeUnit unit ()
            {
                return TimeUnit.MICROSECONDS;
            }

            @Override
            String suffix ()
            {
                return "us";
            }
        },
        MS
        {
            @Override
            TimeUnit unit ()
            {
                return TimeUnit.MILLISECONDS;
            }

            @Override
            String suffix ()
            {
                return "ms";
            }
        },
        S
        {
            @Override
            TimeUnit unit ()
            {
                return TimeUnit.SECONDS;
            }

            @Override
            String suffix ()
            {
                return "s";
            }
        },
        M
        {
            @Override
            TimeUnit unit ()
            {
                return TimeUnit.MINUTES;
            }

            @Override
            String suffix ()
            {
                return "m";
            }
        },
        H
        {
            @Override
            TimeUnit unit ()
            {
                return TimeUnit.HOURS;
            }

            @Override
            String suffix ()
            {
                return "h";
            }
        },
        D
        {
            @Override
            TimeUnit unit ()
            {
                return TimeUnit.DAYS;
            }

            @Override
            String suffix ()
            {
                return "d";
            }
        };

        abstract TimeUnit unit ();

        abstract String suffix ();

        static ParsedTimeDuration unitFor (String s)
        {
            for (ParsedTimeDuration ptd : values ())
            {
                // iteration order is in decl order, so SECONDS matched last
                if (s.endsWith (ptd.suffix ()))
                {
                    return ptd;
                }
            }
            return null;
        }

        static ParsedTimeDuration unitFor (TimeUnit unit)
        {
            for (ParsedTimeDuration ptd : values ())
            {
                if (ptd.unit () == unit)
                {
                    return ptd;
                }
            }
            return null;
        }
    }

    public void setTimeDuration (String name, long value, TimeUnit unit)
    {
    }

    public long getTimeDuration (String name, long defaultValue, TimeUnit unit)
    {
        return defaultValue;
    }

    public long getTimeDuration (String name, String defaultValue, TimeUnit unit)
    {
        return getTimeDuration (name, defaultValue, unit, unit);
    }

    public long getTimeDuration (String name, long defaultValue, TimeUnit defaultUnit, TimeUnit returnUnit)
    {
        return defaultValue;
    }

    public long getTimeDuration (String name, String defaultValue, TimeUnit defaultUnit, TimeUnit returnUnit)
    {
        return getTimeDurationHelper (name, defaultValue, defaultUnit, returnUnit);
    }

    /**
     * Return time duration in the given time unit. Valid units are encoded in
     * properties as suffixes: nanoseconds (ns), microseconds (us), milliseconds
     * (ms), seconds (s), minutes (m), hours (h), and days (d).
     *
     * @param name
     *            Property name
     * @param vStr
     *            The string value with time unit suffix to be converted.
     * @param unit
     *            Unit to convert the stored property, if it exists.
     */
    public long getTimeDurationHelper (String name, String vStr, TimeUnit unit)
    {
        return getTimeDurationHelper (name, vStr, unit, unit);
    }

    /**
     * Return time duration in the given time unit. Valid units are encoded in
     * properties as suffixes: nanoseconds (ns), microseconds (us), milliseconds
     * (ms), seconds (s), minutes (m), hours (h), and days (d).
     *
     * @param name
     *            Property name
     * @param vStr
     *            The string value with time unit suffix to be converted.
     * @param defaultUnit
     *            Unit to convert the stored property, if it exists.
     * @param returnUnit
     *            Unit for the returned value.
     */
    private long getTimeDurationHelper (String name, String vStr, TimeUnit defaultUnit, TimeUnit returnUnit)
    {
        vStr = vStr.trim ();
        vStr = StringUtils.toLowerCase (vStr);
        ParsedTimeDuration vUnit = ParsedTimeDuration.unitFor (vStr);
        if (null == vUnit)
        {
            vUnit = ParsedTimeDuration.unitFor (defaultUnit);
        }
        else
        {
            vStr = vStr.substring (0, vStr.lastIndexOf (vUnit.suffix ()));
        }

        long raw = Long.parseLong (vStr);
        long converted = returnUnit.convert (raw, vUnit.unit ());
        if (vUnit.unit ().convert (converted, returnUnit) < raw)
        {
            logDeprecation ("Possible loss of precision converting " + vStr + vUnit.suffix () + " to " + returnUnit + " for " + name);
        }
        return converted;
    }

    public long[] getTimeDurations (String name, TimeUnit unit)
    {
        String[] strings = getTrimmedStrings (name);
        long[] durations = new long[strings.length];
        for (int i = 0; i < strings.length; i++)
        {
            durations[i] = getTimeDurationHelper (name, strings[i], unit);
        }
        return durations;
    }

    public double getStorageSize (String name, String defaultValue, StorageUnit targetUnit)
    {
        StorageSize measure = StorageSize.parse (defaultValue);
        return convertStorageUnit (measure.getValue (), measure.getUnit (), targetUnit);
    }

    public double getStorageSize (String name, double defaultValue, StorageUnit targetUnit)
    {
        return targetUnit.getDefault (defaultValue);

    }

    public void setStorageSize (String name, double value, StorageUnit unit)
    {
    }

    private double convertStorageUnit (double value, StorageUnit sourceUnit, StorageUnit targetUnit)
    {
        double byteValue = sourceUnit.toBytes (value);
        return targetUnit.fromBytes (byteValue);
    }

    public Pattern getPattern (String name, Pattern defaultValue)
    {
        return defaultValue;
    }

    public void setPattern (String name, Pattern pattern)
    {
    }

    @InterfaceStability.Unstable
    public synchronized String[] getPropertySources (String name)
    {
        return null;
    }

    public static class IntegerRanges implements Iterable<Integer>
    {
        private static class Range
        {
            int start;
            int end;
        }

        private static class RangeNumberIterator implements Iterator<Integer>
        {
            Iterator<Range> internal;
            int at;
            int end;

            public RangeNumberIterator (List<Range> ranges)
            {
                if (ranges != null)
                {
                    internal = ranges.iterator ();
                }
                at = -1;
                end = -2;
            }

            @Override
            public boolean hasNext ()
            {
                if (at <= end)
                {
                    return true;
                }
                else if (internal != null)
                {
                    return internal.hasNext ();
                }
                return false;
            }

            @Override
            public Integer next ()
            {
                if (at <= end)
                {
                    at++;
                    return at - 1;
                }
                else if (internal != null)
                {
                    Range found = internal.next ();
                    if (found != null)
                    {
                        at = found.start;
                        end = found.end;
                        at++;
                        return at - 1;
                    }
                }
                return null;
            }

            @Override
            public void remove ()
            {
                throw new UnsupportedOperationException ();
            }
        };

        List<Range> ranges = new ArrayList<Range> ();

        public IntegerRanges ()
        {
        }

        public IntegerRanges (String newValue)
        {
            StringTokenizer itr = new StringTokenizer (newValue, ",");
            while (itr.hasMoreTokens ())
            {
                String rng = itr.nextToken ().trim ();
                String[] parts = rng.split ("-", 3);
                if (parts.length < 1 || parts.length > 2)
                {
                    throw new IllegalArgumentException ("integer range badly formed: " + rng);
                }
                Range r = new Range ();
                r.start = convertToInt (parts[0], 0);
                if (parts.length == 2)
                {
                    r.end = convertToInt (parts[1], Integer.MAX_VALUE);
                }
                else
                {
                    r.end = r.start;
                }
                if (r.start > r.end)
                {
                    throw new IllegalArgumentException ("IntegerRange from " + r.start + " to " + r.end + " is invalid");
                }
                ranges.add (r);
            }
        }

        private static int convertToInt (String value, int defaultValue)
        {
            String trim = value.trim ();
            if (trim.length () == 0)
            {
                return defaultValue;
            }
            return Integer.parseInt (trim);
        }

        public boolean isIncluded (int value)
        {
            for (Range r : ranges)
            {
                if (r.start <= value && value <= r.end)
                {
                    return true;
                }
            }
            return false;
        }

        public boolean isEmpty ()
        {
            return ranges == null || ranges.isEmpty ();
        }

        @Override
        public String toString ()
        {
            StringBuilder result = new StringBuilder ();
            boolean first = true;
            for (Range r : ranges)
            {
                if (first)
                {
                    first = false;
                }
                else
                {
                    result.append (',');
                }
                result.append (r.start);
                result.append ('-');
                result.append (r.end);
            }
            return result.toString ();
        }

        /**
         * Get range start for the first integer range.
         *
         * @return range start.
         */
        public int getRangeStart ()
        {
            if (ranges == null || ranges.isEmpty ())
            {
                return -1;
            }
            Range r = ranges.get (0);
            return r.start;
        }

        @Override
        public Iterator<Integer> iterator ()
        {
            return new RangeNumberIterator (ranges);
        }

    }

    public IntegerRanges getRange (String name, String defaultValue)
    {
        return new IntegerRanges (get (name, defaultValue));
    }

    public Collection<String> getStringCollection (String name)
    {
        return new ArrayList<String> ();
    }

    public String[] getStrings (String name)
    {
        return null;
    }

    public String[] getStrings (String name, String... defaultValue)
    {
        return defaultValue;
    }

    public Collection<String> getTrimmedStringCollection (String name)
    {
        return new ArrayList<String> ();
    }

    public String[] getTrimmedStrings (String name)
    {
        String valueString = get (name);
        return StringUtils.getTrimmedStrings (valueString);
    }

    public String[] getTrimmedStrings (String name, String... defaultValue)
    {
        String valueString = get (name);
        if (null == valueString)
        {
            return defaultValue;
        }
        else
        {
            return StringUtils.getTrimmedStrings (valueString);
        }
    }
    public void setStrings (String name, String... values)
    {
    }

    public char[] getPassword (String name) throws IOException
    {
        return null;
    }

    public char[] getPasswordFromCredentialProviders (String name) throws IOException
    {
        return null;
    }

    protected char[] getPasswordFromConfig (String name)
    {
        return null;
    }

    public InetSocketAddress getSocketAddr (String hostProperty, String addressProperty, String defaultAddressValue, int defaultPort)
    {
        return null;
    }

    public InetSocketAddress getSocketAddr (String name, String defaultAddress, int defaultPort)
    {
        return null;
    }

    public void setSocketAddr (String name, InetSocketAddress addr)
    {
    }

    public InetSocketAddress updateConnectAddr (String hostProperty, String addressProperty, String defaultAddressValue, InetSocketAddress addr)
    {
        return null;
    }

    public InetSocketAddress updateConnectAddr (String name, InetSocketAddress addr)
    {
        return null;
    }

    public Class<?> getClassByName (String name) throws ClassNotFoundException
    {
        Class<?> ret = getClassByNameOrNull (name);
        if (ret == null)
        {
            throw new ClassNotFoundException ("Class " + name + " not found");
        }
        return ret;
    }

    public Class<?> getClassByNameOrNull (String name)
    {
        Map<String, WeakReference<Class<?>>> map;

        synchronized (CACHE_CLASSES)
        {
            map = CACHE_CLASSES.get (classLoader);
            if (map == null)
            {
                map = Collections.synchronizedMap (new WeakHashMap<String, WeakReference<Class<?>>> ());
                CACHE_CLASSES.put (classLoader, map);
            }
        }

        Class<?> clazz = null;
        WeakReference<Class<?>> ref = map.get (name);
        if (ref != null)
        {
            clazz = ref.get ();
        }

        if (clazz == null)
        {
            try
            {
                clazz = Class.forName (name, true, classLoader);
            }
            catch (ClassNotFoundException e)
            {
                // Leave a marker that the class isn't found
                map.put (name, new WeakReference<Class<?>> (NEGATIVE_CACHE_SENTINEL));
                return null;
            }
            // two putters can race here, but they'll put the same class
            map.put (name, new WeakReference<Class<?>> (clazz));
            return clazz;
        }
        else if (clazz == NEGATIVE_CACHE_SENTINEL)
        {
            return null; // not found
        }
        else
        {
            // cache hit
            return clazz;
        }
    }

    public Class<?>[] getClasses (String name, Class<?>... defaultValue)
    {
        String valueString = getRaw (name);
        if (null == valueString)
        {
            return defaultValue;
        }
        String[] classnames = getTrimmedStrings (name);
        try
        {
            Class<?>[] classes = new Class<?>[classnames.length];
            for (int i = 0; i < classnames.length; i++)
            {
                classes[i] = getClassByName (classnames[i]);
            }
            return classes;
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException (e);
        }
    }

    public Class<?> getClass (String name, Class<?> defaultValue)
    {
        String valueString = getTrimmed (name);
        if (valueString == null)
            return defaultValue;
        try
        {
            return getClassByName (valueString);
        }
        catch (ClassNotFoundException e)
        {
            throw new RuntimeException (e);
        }
    }

    public <U> Class<? extends U> getClass (String name, Class<? extends U> defaultValue, Class<U> xface)
    {
        try
        {
            Class<?> theClass = getClass (name, defaultValue);
            if (theClass != null && !xface.isAssignableFrom (theClass))
                throw new RuntimeException (theClass + " not " + xface.getName ());
            else if (theClass != null)
                return theClass.asSubclass (xface);
            else
                return null;
        }
        catch (Exception e)
        {
            throw new RuntimeException (e);
        }
    }

    @SuppressWarnings ("unchecked")
    public <U> List<U> getInstances (String name, Class<U> xface)
    {
        List<U> ret = new ArrayList<U> ();
        Class<?>[] classes = getClasses (name);
        for (Class<?> cl : classes)
        {
            if (!xface.isAssignableFrom (cl))
            {
                throw new RuntimeException (cl + " does not implement " + xface);
            }
            ret.add ((U) ReflectionUtils.newInstance (cl, this));
        }
        return ret;
    }

    public void setClass (String name, Class<?> theClass, Class<?> xface)
    {
    }

    public Path getLocalPath (String dirsProp, String path) throws IOException
    {
        String[] dirs = getTrimmedStrings (dirsProp);
        int hashCode = path.hashCode ();
        FileSystem fs = FileSystem.getLocal (this);
        for (int i = 0; i < dirs.length; i++)
        { // try each local dir
            int index = (hashCode + i & Integer.MAX_VALUE) % dirs.length;
            Path file = new Path (dirs[index], path);
            Path dir = file.getParent ();
            if (fs.mkdirs (dir) || fs.exists (dir))
            {
                return file;
            }
        }
        throw new IOException ("No valid local directories in property: " + dirsProp);
    }

    /**
     * Get a local file name under a directory named in <i>dirsProp</i> with the
     * given <i>path</i>. If <i>dirsProp</i> contains multiple directories, then
     * one is chosen based on <i>path</i>'s hash code. If the selected directory
     * does not exist, an attempt is made to create it.
     *
     * @param dirsProp
     *            directory in which to locate the file.
     * @param path
     *            file-path.
     * @return local file under the directory with the given path.
     */
    public File getFile (String dirsProp, String path) throws IOException
    {
        String[] dirs = getTrimmedStrings (dirsProp);
        int hashCode = path.hashCode ();
        for (int i = 0; i < dirs.length; i++)
        { // try each local dir
            int index = (hashCode + i & Integer.MAX_VALUE) % dirs.length;
            File file = new File (dirs[index], path);
            File dir = file.getParentFile ();
            if (dir.exists () || dir.mkdirs ())
            {
                return file;
            }
        }
        throw new IOException ("No valid local directories in property: " + dirsProp);
    }

    /**
     * Get the {@link URL} for the named resource.
     *
     * @param name
     *            resource name.
     * @return the url for the named resource.
     */
    public URL getResource (String name)
    {
        return classLoader.getResource (name);
    }

    public InputStream getConfResourceAsInputStream (String name)
    {
        return null;
    }

    public Reader getConfResourceAsReader (String name)
    {
        return null;
    }

    public Set<String> getFinalParameters ()
    {
        Set<String> setFinalParams = Collections.newSetFromMap (new ConcurrentHashMap<String, Boolean> ());
        setFinalParams.addAll (finalParameters);
        return setFinalParams;
    }

    protected synchronized Properties getProps ()
    {
        if (properties == null)
        {
            properties = new Properties ();
        }
        return properties;
    }

    public int size ()
    {
        return 0;
    }

    public void clear ()
    {
    }

    @Override
    public Iterator<Map.Entry<String, String>> iterator ()
    {
        Map<String, String> result = new HashMap<String, String> ();
        return result.entrySet ().iterator ();
    }

    public Map<String, String> getPropsWithPrefix (String confPrefix)
    {
        return new HashMap<> ();
    }

    public void addTags (Properties prop)
    {
    }

    public void writeXml (OutputStream out) throws IOException
    {
    }

    public void writeXml (Writer out) throws IOException
    {
    }

    public void writeXml (@Nullable String propertyName, Writer out) throws IOException, IllegalArgumentException
    {
    }

    public static void dumpConfiguration (Configuration config, String propertyName, Writer out) throws IOException
    {
    }

    public static void dumpConfiguration (Configuration config, Writer out) throws IOException
    {
    }

    public ClassLoader getClassLoader ()
    {
        return classLoader;
    }

    public void setClassLoader (ClassLoader classLoader)
    {
        this.classLoader = classLoader;
    }

    @Override
    public String toString ()
    {
        return "";
    }

    public synchronized void setQuietMode (boolean quietmode)
    {
    }

    synchronized boolean getQuietMode ()
    {
        return true;
    }

    public static void main (String[] args) throws Exception
    {
    }

    @Override
    public void readFields (DataInput in) throws IOException
    {
    }

    @Override
    public void write (DataOutput out) throws IOException
    {
    }

    public Map<String, String> getValByRegex (String regex)
    {
        return new HashMap<String, String> ();
    }

    private static abstract class NegativeCacheSentinel
    {
    }

    public static void dumpDeprecatedKeys ()
    {
    }

    public static boolean hasWarnedDeprecation (String name)
    {
        return false;
    }

    public Properties getAllPropertiesByTag (final String tag)
    {
        return new Properties ();
    }

    public Properties getAllPropertiesByTags (final List<String> tagList)
    {
        return new Properties ();
    }

    public boolean isPropertyTag (String tagStr)
    {
        return false;
    }
}
