<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0" xmlns:s="http://splitcells.net/sew.xsd"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:skos="http://www.w3.org/2004/02/skos/core#" xmlns:svg="http://www.w3.org/2000/svg"
                xmlns="http://www.w3.org/1999/xhtml" xmlns:x="http://www.w3.org/1999/xhtml"
                xmlns:d="http://splitcells.net/den.xsd"
                xmlns:p="http://splitcells.net/private.xsd" xmlns:m="http://www.w3.org/1998/Math/MathML"
                xmlns:r="http://splitcells.net/raw.xsd" xmlns:n="http://splitcells.net/natural.xsd"
                xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xl="http://www.w3.org/1999/xlink"
                xmlns:ns="http://splitcells.net/namespace.xsd">
    <xsl:variable name="net-splitcells-website-server-config-menu-detailed">
        <s:chapter>
            <s:title>Sections</s:title>
            <s:option>
                <s:name>Privacy Policy</s:name>
                <s:page>/net/splitcells/martins/avots/distro/legal/privacy-policy</s:page>
            </s:option>
            <s:option>
                <s:name>Impressum</s:name>
                <s:page>/net/splitcells/martins/avots/distro/legal/impressum</s:page>
            </s:option>
            <s:option>
                <s:name>Licensing</s:name>
                <s:page>/net/splitcells/martins/avots/distro/legal/licensing</s:page>
            </s:option>
            <s:option>
                <s:name>Main Page</s:name>
                <s:page>/net/splitcells/website/server/front-menu</s:page>
            </s:option>
            <div class="messages">
                <h3>Messages</h3>
                <div class="noScriptMessage TextCell text_error">
                    Activate Javascript in order to enable all functions of this site.
                </div>
                <br/>
            </div>
            <a class="net-splitcells-button net-splitcells-component-priority-3 net-splitcells-network-status">
                <xsl:attribute name="href">
                    <xsl:value-of
                            select="s:default-root-relative-url('net/splitcells/network/status.html')"/>
                </xsl:attribute>
            </a>
        </s:chapter>
    </xsl:variable>
</xsl:stylesheet>