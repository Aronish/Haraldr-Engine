# Haraldr Engine
<p>
A simple engine made in OpenGL with the sole purpose of teaching me how graphics and game programming works.
Some files used by IntelliJ IDEA are gitignored but it should be fairly straight forward to clone and build the project if you know what you're doing. I can't give any advice if you're using some other IDE than IntelliJ IDEA, that's up to you.
Java JDK is not included, but LWJGL is.

To run a client, it has to subclass EntryPoint and that subclass has to be set as Main class when running.
The subclass should give an instance of Application to EntryPoint#application through a static initializer. 
</p>
<hr/>

![Preview](preview.png?raw=true)
<p>Preview of the 3D renderer using <a href="http://artisaverb.info/PBT.html">Andrew Maximov's gun model</a> with a PBR material.</p>
<hr/>
<h3>Known Bugs:</h3>
<ul>
    <li>NVIDIA Game Ready Driver version 452.06 running on a Gigabyte 750 Ti
    is confirmed to freeze the computer indefinitely when generating high resolution cubemaps.
    This might be true for more graphics cards. In case problems occur, use version 451.57.
    </li>
    <li>
    Regarding these high resolution cubemaps, depending on the computer a TDR error might be raised
    if the GPU operations take too long.
    <br/>
    (The operating system usually assumes something went wrong if nothing gets rendered for about 2 seconds.
    This has to be mitigated to use the GPU for raw computation)
    <br/>
    To increase the TDR delay, edit these registry keys or add them if they don't exist.
    <br/>
    <b>Location: Computer\HKEY_LOCAL_MACHINE\SYSTEM\CurrentControlSet\Control\GraphicsDrivers</b>
    <table>
        <tr>
            <td>Key Name:</td>
            <td>Key Value:</td>
        </tr>
        <tr>
            <td>TdrLevel</td>
            <td>3</td>
        </tr>
        <tr>
            <td>TdrDelay</td>
            <td>10 - 20 or more*</td>
        </tr>
        <tr>
            <td>TdrDdiDelay</td>
            <td>10 - 20 or more*</td>
        </tr>
    </table>
    <small><i>*20 seconds should be enough for really high resolution cubemaps, but you can optionally increase it even more</i></small>
    <br/>
    Restart your computer after editing the registry.
    </li>
</ul>
<h3>Libraries:</h3>
<b>Java:</b> JDK 14.0.1 (recommended, required)
<br/>
<b><a href="https://www.lwjgl.org/">LWJGL:</a></b> 3.2.3_12
<br/>
<b><a href="https://github.com/stleary/JSON-java">JSON-java by Sean Leary: </a></b>
<br/>
<b>(Jetbrains Annotations)</b>

<p>Special thanks to Joey de Vries for creating <a href="http://www.learnopengl.com">LearnOpenGL.com</a>!</p>